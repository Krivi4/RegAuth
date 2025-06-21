package ru.krivi4.regauth.web.controllers;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.krivi4.regauth.dtos.PersonDto;
import ru.krivi4.regauth.jwt.util.JwtUtil;
import ru.krivi4.regauth.services.login.LastLoginUpdateService;
import ru.krivi4.regauth.services.otp.OtpSendService;
import ru.krivi4.regauth.services.otp.OtpVerifyService;
import ru.krivi4.regauth.adapters.mappers.PersonMapper;
import ru.krivi4.regauth.dtos.AuthenticationDto;
import ru.krivi4.regauth.dtos.VerifyOtpDto;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.models.RefreshToken;
import ru.krivi4.regauth.security.auth.PersonDetails;
import ru.krivi4.regauth.services.person.PersonFindService;
import ru.krivi4.regauth.services.tokens.RefreshTokenService;
import ru.krivi4.regauth.services.auth.RegistrationService;
import ru.krivi4.regauth.util.PersonValidator;
import ru.krivi4.regauth.views.OtpResponse;
import ru.krivi4.regauth.views.TokenResponse;
import ru.krivi4.regauth.web.exceptions.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/** Контроллер для аутентификации и регистрации пользователей.*/
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final PersonFindService personFindService;
  private final LastLoginUpdateService lastLoginUpdateService;
  private final RegistrationService registrationService;
  private final PersonValidator personValidator;
  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;
  private final OtpSendService otpSendService;
  private final OtpVerifyService otpVerifyService;
  private final RefreshTokenService refreshTokenService;
  private final PersonMapper personMapper;

  /**
   * Начало процесса регистрации: валидация входящих данных
   * Отправка смс с кодом подтверждения на телефон
   * Отправка Otp-токена
   * Отправка Otp id
   */
  @PostMapping("/registration")
  public OtpResponse registrationNotVerify(
    @RequestBody @Valid PersonDto personDto,
    BindingResult bindingResult
  ){

    Person person = personMapper.toEntity(personDto);
    personValidator.validate(person, bindingResult);
    if(bindingResult.hasErrors()) {
      if (bindingResult.hasErrors()) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : bindingResult.getFieldErrors()) {
          errors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        throw new ValidationException(errors);
      }
    }
    UUID otpId = otpSendService.send(person.getPhoneNumber());

    String otpToken = jwtUtil.generateOtpRegistrationToken(person, otpId);
    return new OtpResponse(otpId.toString(), otpToken);
  }
  /**
  * Подтверждение регистрации:
   * Проверка Otp-токена в Headers Authorization
   * Проверка OTp id
   * Проверка кода из смс
   * Выдача токенов: access и refresh
   */
  @PostMapping("/registration/verify")
  public TokenResponse registrationVerify(
    @RequestBody VerifyOtpDto verifyOtpDto,
    @RequestHeader ("Authorization") String auth
  ){

    String otpToken = auth.substring(7);
    DecodedJWT decodedJWT = jwtUtil.decode(otpToken);

    if(!decodedJWT.getClaim("phase").asString().equals("OTP_PENDING")){
      throw new TypeTokenInvalidException("Otp");
    }

    UUID otpIdJWT = UUID.fromString(decodedJWT.getClaim("idOtp").asString());
    UUID otpIdReq = verifyOtpDto.getIdOtp();
    String codeReq = verifyOtpDto.getCode();

    if(!otpIdJWT.equals(otpIdReq) || !otpVerifyService.verify(otpIdJWT,codeReq)){
      throw new OtpTokenInvalidException();
    }

   Person person = registrationService.register(decodedJWT);
    String access = jwtUtil.generateAccessToken(person.getUsername());
    String refresh = jwtUtil.generateRefreshToken(person.getUsername());
    refreshTokenService.save(refresh);
    return new TokenResponse(access,refresh);
  }

  /**
   * Начало входа: проверка логина/пароля
   * Отправка смс с кодом подтверждения на телефон
   * Отправка Otp-токена
   * Отправка Otp id
   */
  @PostMapping("/login")
  public OtpResponse LoginNotVerify(@RequestBody AuthenticationDto authenticationDto) { //принимаем логин и пароль и выдаем новый джвт с новым сроком годности
    UsernamePasswordAuthenticationToken authenticationToken =
      new UsernamePasswordAuthenticationToken(
        authenticationDto.getUsername().toLowerCase(Locale.ROOT),
        authenticationDto.getPassword()
      );

      Authentication authentication;
      try {
       authentication =  authenticationManager.authenticate(authenticationToken);
      } catch (BadCredentialsException e) {
        throw new LoginBadCredentialsException();
      }

      PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
      Person person = personDetails.getPerson();
      UUID otpId = otpSendService.send(person.getPhoneNumber());
      String otpToken = jwtUtil.generateOtpLoginToken(person.getUsername(), otpId);

      return new OtpResponse(otpId.toString(), otpToken);

  }
  /**
   * Подтверждение входа:
   * Проверка Otp-токена в Headers Authorization
   * Проверка OTp id
   * Проверка кода из смс
   * Выдача токенов: access и refresh
   */
  @PostMapping("/login/verify")
  public TokenResponse loginVerify(
    @RequestBody VerifyOtpDto verifyOtpDto,
    @RequestHeader ("Authorization") String auth
  ){

    String otpToken = auth.substring(7);
    DecodedJWT decodedJWT = jwtUtil.decode(otpToken);
    if(!decodedJWT.getClaim("phase").asString().equals("OTP_PENDING")){
      throw new TypeTokenInvalidException("Otp");
    }
    UUID otpIdJWT = UUID.fromString(decodedJWT.getClaim("idOtp").asString());
    UUID otpIdReq = verifyOtpDto.getIdOtp();
    String codeReq = verifyOtpDto.getCode();
    String username = decodedJWT.getClaim("username").asString();

    if(!otpIdJWT.equals(otpIdReq) || !otpVerifyService.verify(otpIdJWT,codeReq)){
      throw new OtpTokenInvalidException();
    }
    Person person = personFindService.findByUsername(username);
    lastLoginUpdateService.updatedLastLogin(person);
    String access  = jwtUtil.generateAccessToken(person.getUsername());
    String refresh = jwtUtil.generateRefreshToken(person.getUsername());
    refreshTokenService.save(refresh);

    return new TokenResponse(access, refresh);
}
  /**
   * Обновление токенов:
   * Проверка и отзыв старого refresh
   * Выдача новых access и refresh токенов.
   */
  @PostMapping("/refresh")
  public TokenResponse refresh(@RequestHeader ("Authorization") String auth){
    String refreshTokenRequest = auth.substring(7);
    DecodedJWT decodedJWT = jwtUtil.decode(refreshTokenRequest);
    if(!decodedJWT.getClaim("phase").asString().equals("REFRESH")){
      throw new TypeTokenInvalidException("REFRESH");
    }

   RefreshToken refreshToken;
    try {
      refreshToken = refreshTokenService.validate(refreshTokenRequest);
    } catch (InvalidClaimException e) {
      throw new RefreshTokenInvalidException();
    }

    refreshTokenService.revoked(refreshToken);
    String refreshTokenNew = jwtUtil.generateRefreshToken(refreshToken.getUsername());
    String accessTokenNew = jwtUtil.generateAccessToken(refreshToken.getUsername());
    refreshTokenService.save(refreshTokenNew);

    return new TokenResponse(accessTokenNew, refreshTokenNew);
  }
}

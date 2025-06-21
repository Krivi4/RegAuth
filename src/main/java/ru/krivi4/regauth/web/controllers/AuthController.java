package ru.krivi4.regauth.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.krivi4.regauth.dtos.PersonDto;
import ru.krivi4.regauth.services.auth.AuthService;
import ru.krivi4.regauth.dtos.AuthenticationDto;
import ru.krivi4.regauth.dtos.VerifyOtpDto;
import ru.krivi4.regauth.views.OtpResponse;
import ru.krivi4.regauth.views.TokenResponse;

import javax.validation.Valid;


/** Контроллер для аутентификации и регистрации пользователей.*/
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  /** Запускает процесс регистрации (SMS + OTP-токен). */
  @PostMapping("/registration")
  public ResponseEntity<OtpResponse> registrationNotVerify(
    @RequestBody @Valid PersonDto personDto, BindingResult bindingResult
  ){

    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(authService.registrationNotVerify(personDto, bindingResult));
  }

  /** Подтверждает регистрации и выдаёт пару JWT. */
  @PostMapping("/registration/verify")
  public ResponseEntity<TokenResponse> registrationVerify(
    @RequestBody VerifyOtpDto verifyOtpDto,
    @RequestHeader ("Authorization") String auth
  ){

    return ResponseEntity.ok(authService.registrationVerify(verifyOtpDto, auth));
  }

  /** Проверяет учётные данные и отправляет OTP-код. */
  @PostMapping("/login")
  public ResponseEntity<OtpResponse> LoginNotVerify(
    @RequestBody AuthenticationDto authenticationDto) {

      return ResponseEntity.ok(authService.LoginNotVerify(authenticationDto));
  }

  /** Подтверждает вход и выдаёт пару JWT. */
  @PostMapping("/login/verify")
  public ResponseEntity<TokenResponse> loginVerify(
    @RequestBody VerifyOtpDto verifyOtpDto,
    @RequestHeader ("Authorization") String auth
  ){

    return ResponseEntity.ok(authService.loginVerify(verifyOtpDto, auth));
}

  /** Обновляет access/refresh-токены. */
  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refresh(@RequestHeader ("Authorization") String auth){

    return ResponseEntity.ok(authService.refresh(auth));
  }
}

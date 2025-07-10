package ru.krivi4.regauth.services.auth;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.krivi4.regauth.adapters.mappers.PersonMapper;
import ru.krivi4.regauth.dtos.AuthenticationDto;
import ru.krivi4.regauth.dtos.PersonDto;
import ru.krivi4.regauth.dtos.VerifyOtpDto;
import ru.krivi4.regauth.jwt.util.JwtUtil;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.models.RefreshToken;
import ru.krivi4.regauth.security.auth.PersonDetails;
import ru.krivi4.regauth.services.login.LastLoginUpdateService;
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.services.otp.OtpSendService;
import ru.krivi4.regauth.services.otp.OtpVerifyService;
import ru.krivi4.regauth.services.person.PersonFindService;
import ru.krivi4.regauth.services.tokens.RefreshTokenService;
import ru.krivi4.regauth.utils.PersonValidator;
import ru.krivi4.regauth.views.OtpResponseView;
import ru.krivi4.regauth.views.TokenResponseView;
import ru.krivi4.regauth.web.exceptions.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Реализация сервиса аутентификации.
 * Управляет процессами регистрации, входа, подтверждения OTP и обновления токенов.
 */
@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String PHASE_OTP_PENDING = "OTP_PENDING";
    private static final String PHASE_REFRESH = "REFRESH";
    private static final String USERNAME_CLAIM = "username";
    private static final String PHASE_CLAIM = "phase";
    private static final String ID_OTP_CLAIM = "idOtp";

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
    private final MessageService messageService;

    /**
     * Начинает регистрацию пользователя: валидация данных,
     * отправка SMS с кодом и возврат OTP-токена для подтверждения.
     */
    @Override
    public OtpResponseView registrationNotVerify(PersonDto personDto, BindingResult bindingResult) {
        validatePersonDto(personDto, bindingResult);
        Person person = mapPersonDtoToEntity(personDto);
        UUID otpId = sendOtp(person.getPhoneNumber());
        String otpToken = generateOtpRegistrationToken(person, otpId);
        return buildOtpResponse(otpId, otpToken);
    }

    /**
     * Завершает регистрацию: проверяет OTP и возвращает пару access/refresh токенов.
     */
    @Override
    public TokenResponseView registrationVerify(VerifyOtpDto verifyOtpDto, String authorizationHeader) {
        DecodedJWT decodedJwt = decodeJwtAndCheckPhase(authorizationHeader, PHASE_OTP_PENDING);
        verifyOtp(decodedJwt, verifyOtpDto);
        Person person = registerUser(decodedJwt);
        return issueTokens(person.getUsername());
    }

    /**
     * Начинает вход пользователя: проверка логина и пароля,
     * отправка OTP и возврат токена для подтверждения.
     */
    @Override
    public OtpResponseView loginNotVerify(AuthenticationDto authenticationDto) {
        Person person = authenticate(authenticationDto);
        UUID otpId = sendOtp(person.getPhoneNumber());
        String otpToken = generateOtpLoginToken(person, otpId);
        return buildOtpResponse(otpId, otpToken);
    }

    /**
     * Завершает вход: проверяет OTP и возвращает пару access/refresh токенов.
     */
    @Override
    public TokenResponseView loginVerify(VerifyOtpDto verifyOtpDto, String authorizationHeader) {
        DecodedJWT decodedJwt = decodeJwtAndCheckPhase(authorizationHeader, PHASE_OTP_PENDING);
        verifyOtp(decodedJwt, verifyOtpDto);
        Person person = loadPersonAndUpdateLogin(decodedJwt);
        return issueTokens(person.getUsername());
    }

    /**
     * Обновляет access и refresh токены.
     */
    @Override
    public TokenResponseView refresh(String authorizationHeader) {
        DecodedJWT decodedJwt = decodeJwtAndCheckPhase(authorizationHeader, PHASE_REFRESH);
        RefreshToken refreshToken = validateRefreshToken(decodedJwt);
        revokeRefreshToken(refreshToken);
        return issueTokens(refreshToken.getUsername());
    }

    /*----------Вспомогательные методы----------*/

    /**
     * Валидирует DTO и выбрасывает ValidationException, если есть ошибки.
     */
    private void validatePersonDto(PersonDto dto, BindingResult result) {
        Person person = personMapper.toEntity(dto);
        personValidator.validate(person, result);
        throwIfErrors(result);
    }

    /**
     * Преобразует PersonDto в сущность Person.
     */
    private Person mapPersonDtoToEntity(PersonDto dto) {
        return personMapper.toEntity(dto);
    }

    /**
     * Отправляет OTP на телефон.
     */
    private UUID sendOtp(String phoneNumber) {
        return otpSendService.send(phoneNumber);
    }

    /**
     * Генерирует OTP-токен для регистрации.
     */
    private String generateOtpRegistrationToken(Person person, UUID otpId) {
        return jwtUtil.generateOtpRegistrationToken(person, otpId);
    }

    /**
     * Генерирует OTP-токен для входа.
     */
    private String generateOtpLoginToken(Person person, UUID otpId) {
        return jwtUtil.generateOtpLoginToken(person.getUsername(), otpId);
    }

    /**
     * Формирует ответ с OTP ID и токеном.
     */
    private OtpResponseView buildOtpResponse(UUID otpId, String otpToken) {
        return new OtpResponseView(otpId.toString(), otpToken);
    }

    /**
     * Аутентифицирует пользователя по логину и паролю.
     */
    private Person authenticate(AuthenticationDto dto) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                dto.getUsername().toLowerCase(Locale.ROOT),
                dto.getPassword()
        );
        try {
            Authentication auth = authenticationManager.authenticate(token);
            return ((PersonDetails) auth.getPrincipal()).getPerson();
        } catch (BadCredentialsException ex) {
            throw new LoginBadCredentialsException(messageService);
        }
    }

    /**
     * Декодирует JWT и проверяет, что он находится в нужной фазе.
     */
    private DecodedJWT decodeJwtAndCheckPhase(String header, String expectedPhase) {
        DecodedJWT jwt = jwtUtil.decode(stripBearerPrefix(header));
        String actualPhase = jwt.getClaim(PHASE_CLAIM).asString();
        if (!expectedPhase.equals(actualPhase)) {
            throw new TypeTokenInvalidException(expectedPhase, messageService);
        }
        return jwt;
    }

    /**
     * Проверяет OTP-код и идентификатор.
     */
    private void verifyOtp(DecodedJWT jwt, VerifyOtpDto dto) {
        UUID jwtOtpId = UUID.fromString(jwt.getClaim(ID_OTP_CLAIM).asString());
        if (!jwtOtpId.equals(dto.getIdOtp()) ||
                !otpVerifyService.verify(jwtOtpId, dto.getCode())) {
            throw new OtpTokenInvalidException(messageService);
        }
    }

    /**
     * Регистрирует нового пользователя.
     */
    private Person registerUser(DecodedJWT jwt) {
        return registrationService.register(jwt);
    }

    /**
     * Загружает пользователя и обновляет дату последнего входа.
     */
    private Person loadPersonAndUpdateLogin(DecodedJWT jwt) {
        String username = jwt.getClaim(USERNAME_CLAIM).asString();
        Person person = personFindService.findByUsername(username);
        lastLoginUpdateService.updatedLastLogin(person);
        return person;
    }

    /**
     * Проверяет refresh-токен.
     */
    private RefreshToken validateRefreshToken(DecodedJWT jwt) {
        try {
            return refreshTokenService.validate(stripBearerPrefix(jwt.getToken()));
        } catch (InvalidClaimException e) {
            throw new RefreshTokenInvalidException(messageService);
        }
    }

    /**
     * Отзывает refresh-токен.
     */
    private void revokeRefreshToken(RefreshToken token) {
        refreshTokenService.revoked(token);
    }

    /**
     * Генерирует и сохраняет новую пару токенов.
     */
    private TokenResponseView issueTokens(String username) {
        String access = jwtUtil.generateAccessToken(username);
        String refresh = jwtUtil.generateRefreshToken(username);
        refreshTokenService.save(refresh);
        return new TokenResponseView(access, refresh);
    }

    /**
     * Убирает префикс Bearer из строки Authorization.
     */
    private String stripBearerPrefix(String header) {
        return header.substring(BEARER_PREFIX.length()).trim();
    }

    /**
     * Выбрасывает ValidationException, если есть ошибки в BindingResult.
     */
    private void throwIfErrors(BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.putIfAbsent(error.getField(), error.getDefaultMessage());
            }
            throw new ValidationException(errors, messageService);
        }
    }
}

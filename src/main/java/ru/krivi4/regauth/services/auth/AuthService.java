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

@Service
@RequiredArgsConstructor
public class AuthService {

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

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String PHASE_OTP_PENDING = "OTP_PENDING";
    private static final String PHASE_REFRESH = "REFRESH";
    private static final String USERNAME_CLAIM = "username";
    private static final String PHASE_CLAIM = "phase";
    private static final String ID_OTP_CLAIM = "idOtp";


    /**
     * Начало процесса регистрации: валидация входящих данных
     * Отправка смс с кодом подтверждения на телефон
     * Отправка Otp-токена
     * Отправка Otp id
     */
    public OtpResponseView registrationNotVerify(PersonDto personDto,
                                                 BindingResult bindingResult) {

        Person person = validateAndMapPerson(personDto, bindingResult);

        UUID otpId = sendOtpAndReturnId(person.getPhoneNumber());
        String otpToken = jwtUtil.generateOtpRegistrationToken(person, otpId);

        return buildOtpResponse(otpId, otpToken);
    }

    /**
     * Подтверждение регистрации:
     * Проверка Otp-токена в Headers Authorization
     * Проверка OTp id
     * Проверка кода из смс
     * Выдача токенов: access и refresh
     */
    public TokenResponseView registrationVerify(VerifyOtpDto verifyOtpDto,
                                                String authorizationHeader) {

        DecodedJWT decodedJwt = decodeAndCheckPhase(authorizationHeader, PHASE_OTP_PENDING);

        validateOtpPairAndCode(decodedJwt, verifyOtpDto);

        Person person = registrationService.register(decodedJwt);
        return buildAndPersistTokens(person.getUsername());
    }


    /**
     * Начало входа: проверка логина/пароля
     * Отправка смс с кодом подтверждения на телефон
     * Отправка Otp-токена
     * Отправка Otp id
     */
    public OtpResponseView loginNotVerify(AuthenticationDto authenticationDto) {

        Person person = authenticateCredentialsAndGetPerson(authenticationDto);

        UUID otpId = sendOtpAndReturnId(person.getPhoneNumber());
        String otpToken = jwtUtil.generateOtpLoginToken(person.getUsername(), otpId);

        return buildOtpResponse(otpId, otpToken);
    }

    /**
     * Подтверждение входа:
     * Проверка Otp-токена в Headers Authorization
     * Проверка Otp id
     * Проверка кода из смс
     * Выдача токенов: access и refresh
     */
    public TokenResponseView loginVerify(VerifyOtpDto verifyOtpDto,
                                         String authorizationHeader) {

        DecodedJWT decodedJwt = decodeAndCheckPhase(authorizationHeader, PHASE_OTP_PENDING);

        validateOtpPairAndCode(decodedJwt, verifyOtpDto);

        String username = decodedJwt.getClaim(USERNAME_CLAIM).asString();
        Person person = personFindService.findByUsername(username);

        lastLoginUpdateService.updatedLastLogin(person);

        return buildAndPersistTokens(username);
    }

    /**
     * Обновление токенов:
     * Проверка и отзыв старого refresh
     * Выдача новых access и refresh токенов.
     */
    public TokenResponseView refresh(String authorizationHeader) {

        decodeAndCheckPhase(authorizationHeader, PHASE_REFRESH);
        String rawRefresh = extractBearerToken(authorizationHeader);
        RefreshToken refreshFromDb;

        try {
            refreshFromDb = refreshTokenService.validate(rawRefresh);
        } catch (InvalidClaimException e) {
            throw new RefreshTokenInvalidException(messageService);
        }

        refreshTokenService.revoked(refreshFromDb);
        return buildAndPersistTokens(refreshFromDb.getUsername());
    }

    /* --------------вспомогательные методы --------------*/

    /**
     * Валидирует PersonDto и собирает все ошибки в BindingResult.
     */
    private Person validateAndMapPerson(PersonDto personDto, BindingResult bindingResult) {
        Person person = personMapper.toEntity(personDto);

        personValidator.validate(person, bindingResult);
        collectBindingResultErrors(bindingResult);

        return person;
    }

    /**
     * Отправляет SMS-код через OtpSendService и возвращает сгенерированный ID.
     */
    private UUID sendOtpAndReturnId(String phoneNumber) {
        return otpSendService.send(phoneNumber);
    }

    /**
     * Собирает OtpResponseView из ID и JWT-токена.
     */
    private OtpResponseView buildOtpResponse(UUID otpId, String otpToken) {
        return new OtpResponseView(String.valueOf(otpId), otpToken);
    }

    /**
     * Аутентифицирует логин/пароль и возвращает Person.
     */
    private Person authenticateCredentialsAndGetPerson(AuthenticationDto authenticationDto) {

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        authenticationDto.getUsername().toLowerCase(Locale.ROOT),
                        authenticationDto.getPassword()
                );

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(token);
        } catch (BadCredentialsException e) {
            throw new LoginBadCredentialsException(messageService);
        }

        return ((PersonDetails) authentication.getPrincipal()).getPerson();
    }

    /**
     * Декодирует JWT-токен, проверяет соответствие ожидаемой фазы и возвращает объект.
     */
    private DecodedJWT decodeAndCheckPhase(String authorizationHeader, String expectedPhase) {
        DecodedJWT decodedJwt = jwtUtil.decode(extractBearerToken(authorizationHeader));
        enforcePhase(decodedJwt, expectedPhase);
        return decodedJwt;
    }

    /**
     * Извлекает сам JWT-токен, убирая префикс "Bearer ".
     */
    private String extractBearerToken(String authorizationHeader) {
        return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
    }

    /**
     * Проверяет, что в decodedJwt установлен claim "phase", равный expectedPhase.
     */
    private void enforcePhase(DecodedJWT decodedJwt, String expectedPhase) {
        if (!expectedPhase.equals(decodedJwt.getClaim(PHASE_CLAIM).asString())) {
            throw new TypeTokenInvalidException(expectedPhase, messageService);
        }
    }

    /**
     * Проверяет, что переданный и пришедший по JWT ID OTP совпадают,
     * и что код подтверждения валиден.
     */
    private void validateOtpPairAndCode(DecodedJWT decodedJwt, VerifyOtpDto verifyOtpDto) {

        UUID otpIdJwt = UUID.fromString(decodedJwt.getClaim(ID_OTP_CLAIM).asString());
        UUID otpIdReq = verifyOtpDto.getIdOtp();
        String codeReq = verifyOtpDto.getCode();

        if (!otpIdJwt.equals(otpIdReq) || !otpVerifyService.verify(otpIdJwt, codeReq)) {
            throw new OtpTokenInvalidException(messageService);
        }
    }

    /**
     * Создаёт новый набор access+refresh токенов для username,
     * сохраняет refresh в БД и возвращает TokenResponseView.
     */
    private TokenResponseView buildAndPersistTokens(String username) {
        String accessToken = jwtUtil.generateAccessToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);
        refreshTokenService.save(refreshToken);
        return new TokenResponseView(accessToken, refreshToken);
    }

    /**
     * Считывает ошибки из BindingResult и при наличии выбрасывает ValidationException.
     */
    private void collectBindingResultErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
            }
            throw new ValidationException(errors, messageService);
        }
    }
}

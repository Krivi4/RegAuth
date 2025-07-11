package ru.krivi4.regauth.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.krivi4.regauth.dtos.AuthenticationDto;
import ru.krivi4.regauth.dtos.PersonDto;
import ru.krivi4.regauth.dtos.VerifyOtpDto;
import ru.krivi4.regauth.services.auth.AuthService;
import ru.krivi4.regauth.views.OtpResponseView;
import ru.krivi4.regauth.views.TokenResponseView;

import javax.validation.Valid;

import static ru.krivi4.regauth.web.controllers.AuthController.API_BASE;

/**
 * REST‑контроллер для регистрации, аутентификации и обновления токенов.
 * Обрабатывает все этапы: от регистрации до refresh‑токена.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(API_BASE)
public class AuthController {

    public static final String API_BASE = "/regauth/api/v1/auth";
    private static final String REGISTRATION = "/registration";
    private static final String LOGIN = "/login";
    private static final String VERIFY = "/verify";
    private static final String REFRESH = "/refresh";
    private static final String AUTH_HEADER = "Authorization";

    private final AuthService authService;

    /**
     * Инициирует регистрацию: принимает PersonDto,
     * валидирует его и отправляет OTP-код на телефон.
     */
    @PostMapping(value = REGISTRATION,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OtpResponseView> registrationNotVerify(
            @RequestBody @Valid PersonDto personDto,
            BindingResult bindingResult) {

        OtpResponseView response = handleRegistrationNotVerify(personDto, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Подтверждает регистрацию с помощью OTP-кода и возвращает пару JWT‑токенов.
     */
    @PostMapping(value = REGISTRATION + VERIFY,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponseView> registrationVerify(
            @RequestBody VerifyOtpDto verifyOtpDto,
            @RequestHeader(AUTH_HEADER) String authHeader) {

        TokenResponseView response = handleRegistrationVerify(verifyOtpDto, authHeader);
        return ResponseEntity.ok(response);
    }

    /**
     * Проверяет логин и пароль, отправляет OTP-код на телефон.
     */
    @PostMapping(value = LOGIN,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OtpResponseView> loginNotVerify(
            @RequestBody AuthenticationDto authenticationDto) {

        OtpResponseView response = handleLoginNotVerify(authenticationDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Подтверждает вход с помощью OTP-кода и возвращает пару JWT‑токенов.
     */
    @PostMapping(value = LOGIN + VERIFY,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponseView> loginVerify(
            @RequestBody VerifyOtpDto verifyOtpDto,
            @RequestHeader(AUTH_HEADER) String authHeader) {

        TokenResponseView response = handleLoginVerify(verifyOtpDto, authHeader);
        return ResponseEntity.ok(response);
    }

    /**
     * Обновляет access и refresh‑токены по переданному refresh‑токену.
     */
    @PostMapping(value = REFRESH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponseView> refresh(
            @RequestHeader(AUTH_HEADER) String authHeader) {

        TokenResponseView response = handleRefresh(authHeader);
        return ResponseEntity.ok(response);
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Вызывает сервис для начала регистрации.
     */
    private OtpResponseView handleRegistrationNotVerify(PersonDto personDto, BindingResult bindingResult) {
        return authService.registrationNotVerify(personDto, bindingResult);
    }

    /**
     * Вызывает сервис для подтверждения регистрации.
     */
    private TokenResponseView handleRegistrationVerify(VerifyOtpDto verifyOtpDto, String authHeader) {
        return authService.registrationVerify(verifyOtpDto, authHeader);
    }

    /**
     * Вызывает сервис для начала процесса входа.
     */
    private OtpResponseView handleLoginNotVerify(AuthenticationDto authenticationDto) {
        return authService.loginNotVerify(authenticationDto);
    }

    /**
     * Вызывает сервис для подтверждения входа.
     */
    private TokenResponseView handleLoginVerify(VerifyOtpDto verifyOtpDto, String authHeader) {
        return authService.loginVerify(verifyOtpDto, authHeader);
    }

    /**
     * Вызывает сервис для обновления токенов.
     */
    private TokenResponseView handleRefresh(String authHeader) {
        return authService.refresh(authHeader);
    }
}

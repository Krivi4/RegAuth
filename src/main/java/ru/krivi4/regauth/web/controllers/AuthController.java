package ru.krivi4.regauth.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.krivi4.regauth.dtos.PersonDto;
import ru.krivi4.regauth.services.auth.AuthService;
import ru.krivi4.regauth.dtos.AuthenticationDto;
import ru.krivi4.regauth.dtos.VerifyOtpDto;
import ru.krivi4.regauth.views.OtpResponseView;
import ru.krivi4.regauth.views.TokenResponseView;

import javax.validation.Valid;


/**
 * Контроллер для аутентификации и регистрации пользователей.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Запускает процесс регистрации (SMS + OTP-токен).
     */
    @PostMapping(value = "/registration",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OtpResponseView> registrationNotVerify(
            @RequestBody @Valid PersonDto personDto, BindingResult bindingResult
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.registrationNotVerify(personDto, bindingResult));
    }

    /**
     * Подтверждает регистрации и выдаёт пару JWT.
     */
    @PostMapping(value = "/registration/verify",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponseView> registrationVerify(
            @RequestBody VerifyOtpDto verifyOtpDto,
            @RequestHeader(AUTHORIZATION_HEADER) String auth
    ) {

        return ResponseEntity.ok(authService.registrationVerify(verifyOtpDto, auth));
    }

    /**
     * Проверяет учётные данные и отправляет OTP-код.
     */
    @PostMapping(value = "/login",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OtpResponseView> LoginNotVerify(
            @RequestBody AuthenticationDto authenticationDto) {

        return ResponseEntity.ok(authService.loginNotVerify(authenticationDto));
    }

    /**
     * Подтверждает вход и выдаёт пару JWT.
     */
    @PostMapping(value = "/login/verify",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponseView> loginVerify(
            @RequestBody VerifyOtpDto verifyOtpDto,
            @RequestHeader(AUTHORIZATION_HEADER) String auth
    ) {

        return ResponseEntity.ok(authService.loginVerify(verifyOtpDto, auth));
    }

    /**
     * Обновляет access/refresh-токены.
     */
    @PostMapping(value = "/refresh",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponseView> refresh(
            @RequestHeader(AUTHORIZATION_HEADER) String auth
    ) {
        return ResponseEntity.ok(authService.refresh(auth));
    }
}

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

/**
 * Контроллер аутентификации и регистрации пользователей.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(AuthController.API_BASE)
public class AuthController {

    public static final String API_BASE              = "/api/v1/auth";
    private static final String REGISTRATION         = "/registration";
    private static final String REGISTRATION_VERIFY  = REGISTRATION + "/verify";
    private static final String LOGIN                = "/login";
    private static final String LOGIN_VERIFY         = LOGIN + "/verify";
    private static final String REFRESH              = "/refresh";
    private static final String AUTH_HEADER          = "Authorization";
    private static final String JSON                 = MediaType.APPLICATION_JSON_VALUE;

    private final AuthService authService;


    /** Запускает процесс регистрации (SMS + OTP-токен). */
    @PostMapping(value = REGISTRATION, produces = JSON, consumes = JSON)
    public ResponseEntity<OtpResponseView> registrationNotVerify(
            @RequestBody @Valid PersonDto personDto,
            BindingResult bindingResult) {

        OtpResponseView body = authService.registrationNotVerify(personDto, bindingResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /** Подтверждает регистрацию и выдаёт пару JWT. */
    @PostMapping(value = REGISTRATION_VERIFY, produces = JSON, consumes = JSON)
    public ResponseEntity<TokenResponseView> registrationVerify(
            @RequestBody VerifyOtpDto verifyOtpDto,
            @RequestHeader(AUTH_HEADER) String authHeader) {

        return ResponseEntity.ok(authService.registrationVerify(verifyOtpDto, authHeader));
    }

    /** Проверяет учётные данные и отправляет OTP-код. */
    @PostMapping(value = LOGIN, produces = JSON, consumes = JSON)
    public ResponseEntity<OtpResponseView> loginNotVerify(
            @RequestBody AuthenticationDto authenticationDto) {

        return ResponseEntity.ok(authService.loginNotVerify(authenticationDto));
    }

    /** Подтверждает вход и выдаёт пару JWT. */
    @PostMapping(value = LOGIN_VERIFY, produces = JSON, consumes = JSON)
    public ResponseEntity<TokenResponseView> loginVerify(
            @RequestBody VerifyOtpDto verifyOtpDto,
            @RequestHeader(AUTH_HEADER) String authHeader) {

        return ResponseEntity.ok(authService.loginVerify(verifyOtpDto, authHeader));
    }

    /** Обновляет access/refresh-токены. */
    @PostMapping(value = REFRESH, produces = JSON)
    public ResponseEntity<TokenResponseView> refresh(
            @RequestHeader(AUTH_HEADER) String authHeader) {

        return ResponseEntity.ok(authService.refresh(authHeader));
    }
}

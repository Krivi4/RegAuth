package ru.krivi4.regauth.services.auth;

import ru.krivi4.regauth.dtos.AuthenticationDto;
import ru.krivi4.regauth.dtos.PersonDto;
import ru.krivi4.regauth.dtos.VerifyOtpDto;
import ru.krivi4.regauth.views.OtpResponseView;
import ru.krivi4.regauth.views.TokenResponseView;

/**
 * Контракт сервиса аутентификации.
 * Обеспечивает регистрацию, вход и обновление токенов.
 */
public interface AuthService {

    /**
     * Регистрирует нового пользователя без подтверждения OTP.
     */
    OtpResponseView registrationNotVerify(PersonDto personDto, org.springframework.validation.BindingResult bindingResult);

    /**
     * Завершает регистрацию с подтверждением OTP и возвращает токены.
     */
    TokenResponseView registrationVerify(VerifyOtpDto verifyOtpDto, String authorizationHeader);

    /**
     * Выполняет вход пользователя без подтверждения OTP.
     */
    OtpResponseView loginNotVerify(AuthenticationDto authenticationDto);

    /**
     * Завершает вход с подтверждением OTP и возвращает токены.
     */
    TokenResponseView loginVerify(VerifyOtpDto verifyOtpDto, String authorizationHeader);

    /**
     * Обновляет access-токен с использованием refresh-токена.
     */
    TokenResponseView refresh(String authorizationHeader);
}

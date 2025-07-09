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

    OtpResponseView registrationNotVerify(PersonDto personDto, org.springframework.validation.BindingResult bindingResult);

    TokenResponseView registrationVerify(VerifyOtpDto verifyOtpDto, String authorizationHeader);

    OtpResponseView loginNotVerify(AuthenticationDto authenticationDto);

    TokenResponseView loginVerify(VerifyOtpDto verifyOtpDto, String authorizationHeader);

    TokenResponseView refresh(String authorizationHeader);
}

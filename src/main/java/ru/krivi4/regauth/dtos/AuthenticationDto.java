package ru.krivi4.regauth.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * DTO для входа пользователя: логин и пароль.
 */
@Getter
@Setter
public class AuthenticationDto {

    private static final String MESSAGE_USERNAME_REQUIRED = "{username.required.validation.exception}";
    private static final String MESSAGE_PASSWORD_REQUIRED = "{password.required.validation.exception}";

    @NotNull(message = MESSAGE_USERNAME_REQUIRED)
    private String username;

    @NotNull(message = MESSAGE_PASSWORD_REQUIRED)
    private String password;
}

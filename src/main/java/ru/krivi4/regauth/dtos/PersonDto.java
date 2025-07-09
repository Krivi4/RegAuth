package ru.krivi4.regauth.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * DTO для регистрации пользователя.
 */
@Getter
@Setter
public class PersonDto {

    private static final String MESSAGE_USERNAME_REQUIRED = "{username.required.validation.exception}";
    private static final String MESSAGE_PASSWORD_REQUIRED = "{password.required.validation.exception}";
    private static final String MESSAGE_EMAIL_REQUIRED = "{email.required.validation.exception}";
    private static final String MESSAGE_EMAIL_INVALID = "{email.invalid.validation.exception}";
    private static final String MESSAGE_PHONE_REQUIRED = "{phone.required.validation.exception}";

    @NotNull(message = MESSAGE_USERNAME_REQUIRED)
    private String username;

    @NotNull(message = MESSAGE_PASSWORD_REQUIRED)
    private String password;

    @NotEmpty(message = MESSAGE_EMAIL_REQUIRED)
    @Email(message = MESSAGE_EMAIL_INVALID)
    private String email;

    @NotNull(message = MESSAGE_PHONE_REQUIRED)
    private String phoneNumber;
}

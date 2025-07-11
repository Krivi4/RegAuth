package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**
 * Пароль слишком короткий.
 * HTTP 400 Bad Request.
 */
public class PasswordTooShortException extends ApiException {

    private static final String MSG_KEY = "password.length.validation.exception";

    public PasswordTooShortException(MessageService messageService) {
        super(HttpStatus.BAD_REQUEST, messageService.getMessage(MSG_KEY));
    }
}

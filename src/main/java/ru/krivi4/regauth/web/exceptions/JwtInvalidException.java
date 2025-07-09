package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.DefaultMessageService;

/**
 * Неверный или просроченный JWT (HTTP 400).
 */
public class JwtInvalidException extends ApiException {

    private static final String MSG_KEY = "jwt.invalid.exception";

    public JwtInvalidException(DefaultMessageService ms) {
        super(HttpStatus.BAD_REQUEST, ms.getMessage(MSG_KEY));
    }
}

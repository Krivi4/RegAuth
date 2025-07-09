package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.DefaultMessageService;

/**
 * Тип токена не соответствует ожидаемому.
 * HTTP 400 Bad Request.
 */
public class TypeTokenInvalidException extends ApiException {

    private static final String MSG_KEY = "type.token.invalid.exception";

    public TypeTokenInvalidException(String phaseToken, DefaultMessageService ms) {
        super(HttpStatus.BAD_REQUEST, ms.getMessage(MSG_KEY, phaseToken));
    }
}

package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**
 * Тип токена не соответствует ожидаемому.
 * HTTP 400 Bad Request.
 */
public class TypeTokenInvalidException extends ApiException {

    private static final String MSG_KEY = "type.token.invalid.exception";

    public TypeTokenInvalidException(String phaseToken, MessageService messageService) {
        super(HttpStatus.BAD_REQUEST, messageService.getMessage(MSG_KEY, phaseToken));
    }
}

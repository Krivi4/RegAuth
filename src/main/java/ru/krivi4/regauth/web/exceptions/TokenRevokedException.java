package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**
 * Токен был отозван и не может быть использован.
 * HTTP 401 Unauthorized.
 */
public class TokenRevokedException extends ApiException {

    private static final String MSG_KEY = "token.revoked.exception";

    public TokenRevokedException(MessageService messageService) {
        super(HttpStatus.UNAUTHORIZED, messageService.getMessage(MSG_KEY));
    }
}

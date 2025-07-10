package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**
 * Refresh-токен с указанным идентификатором не найден.
 * HTTP 404 Not Found.
 */
public class RefreshTokenNotFoundException extends ApiException {

    private static final String MSG_KEY = "refresh.token.not.found.exception";

    public RefreshTokenNotFoundException(String tokenId, MessageService messageService) {
        super(HttpStatus.NOT_FOUND, messageService.getMessage(MSG_KEY, tokenId));
    }
}

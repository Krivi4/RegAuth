package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**
 * Refresh-токен не найден в БД (HTTP 404).
 */
public class RefreshTokenNotFoundException extends ApiException {

    public RefreshTokenNotFoundException(String tokenId, MessageService messageService) {
        super(
                HttpStatus.NOT_FOUND,
                messageService.getMessage("refresh.token.not.found.exception", tokenId)
        );
    }
}

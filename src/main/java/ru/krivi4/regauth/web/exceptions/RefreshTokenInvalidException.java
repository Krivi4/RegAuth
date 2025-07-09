package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.DefaultMessageService;

/**
 * Refresh-токен недействителен или отозван.
 * HTTP 401 Unauthorized.
 */
public class RefreshTokenInvalidException extends ApiException {

    private static final String MSG_KEY = "refresh.token.revoked.exception";

    public RefreshTokenInvalidException(DefaultMessageService ms) {
        super(HttpStatus.UNAUTHORIZED, ms.getMessage(MSG_KEY));
    }
}

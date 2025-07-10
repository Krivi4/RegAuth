package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**
 * Фаза JWT равна OTP_PENDING, поэтому аутентификация пропущена (HTTP 401).
 */
public class AuthenticateSkipException extends ApiException {

    private static final String MSG_KEY = "authentication.skip.exception";

    public AuthenticateSkipException(MessageService messageService) {
        super(HttpStatus.UNAUTHORIZED, messageService.getMessage(MSG_KEY));
    }
}
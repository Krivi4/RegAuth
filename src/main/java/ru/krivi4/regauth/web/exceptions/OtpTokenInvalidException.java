package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.DefaultMessageService;

/**
 * Неверный или просроченный Otp (HTTP 401).
 */
public class OtpTokenInvalidException extends ApiException {

    private static final String MSG_KEY = "otp.token.invalid.exception";

    public OtpTokenInvalidException(DefaultMessageService ms) {
        super(HttpStatus.UNAUTHORIZED, ms.getMessage(MSG_KEY));
    }
}
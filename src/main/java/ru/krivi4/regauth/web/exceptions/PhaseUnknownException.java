package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.DefaultMessageService;

/**
 * Неизвестная фаза JWT-токена.
 * HTTP 400 Bad Request.
 */
public class PhaseUnknownException extends ApiException {

    private static final String MSG_KEY = "phase.unknown.exception";

    public PhaseUnknownException(String rawPhase, DefaultMessageService ms) {
        super(HttpStatus.BAD_REQUEST, ms.getMessage(MSG_KEY) + rawPhase);
    }
}

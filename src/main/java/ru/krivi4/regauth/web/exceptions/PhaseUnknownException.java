package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**
 * Неизвестная фаза JWT-токена.
 * HTTP 400 Bad Request.
 */
public class PhaseUnknownException extends ApiException {

    private static final String MSG_KEY = "phase.unknown.exception";

    public PhaseUnknownException(String rawPhase, MessageService messageService) {
        super(HttpStatus.BAD_REQUEST, messageService.getMessage(MSG_KEY) + rawPhase);
    }
}

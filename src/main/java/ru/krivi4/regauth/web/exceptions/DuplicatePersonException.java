package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**
 * Нарушение уникальности пользователя (HTTP 409).
 */
public class DuplicatePersonException extends ApiException {

    private static final String MSG_KEY = "duplicate.person.exception";

    public DuplicatePersonException(String username, MessageService messageService) {
        super(HttpStatus.CONFLICT, messageService.getMessage(MSG_KEY, username));
    }
}

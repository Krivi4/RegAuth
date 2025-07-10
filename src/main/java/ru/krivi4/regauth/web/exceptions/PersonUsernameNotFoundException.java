package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**
 * Пользователь с указанным именем не найден.
 * HTTP 404 Not Found.
 */
public class PersonUsernameNotFoundException extends ApiException {

    private static final String MSG_KEY = "person.username.not.found.exception";

    public PersonUsernameNotFoundException(MessageService messageService) {
        super(HttpStatus.NOT_FOUND, messageService.getMessage(MSG_KEY));
    }
}

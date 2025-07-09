package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.DefaultMessageService;

/**
 * Пользователь с указанным номером телефона не найден.
 * HTTP 404 Not Found.
 */
public class PersonPhoneNotFoundException extends ApiException {

    private static final String MSG_KEY = "person.phone.not.found.exception";

    public PersonPhoneNotFoundException(DefaultMessageService ms) {
        super(HttpStatus.NOT_FOUND, ms.getMessage(MSG_KEY));
    }
}

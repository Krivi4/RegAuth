package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

public class PersonUsernameNotFoundException extends ApiException {

    public PersonUsernameNotFoundException(MessageService messageService) {
        super(
                HttpStatus.NOT_FOUND,
                messageService.getMessage("person.username.not.found.exception")
        );
    }
}

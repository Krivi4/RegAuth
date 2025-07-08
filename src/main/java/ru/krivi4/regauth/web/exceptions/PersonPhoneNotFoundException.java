package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

public class PersonPhoneNotFoundException extends ApiException {

    public PersonPhoneNotFoundException(MessageService messageService) {
        super(
                HttpStatus.NOT_FOUND,
                messageService.getMessage("person.phone.not.found.exception")
        );
    }
}

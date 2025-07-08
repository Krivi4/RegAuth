package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**
 * Исключение, бросаемое при отсутствии в БД роли с указанным именем.
 * HTTP 404 Not Found.
 */
public class DefaultRoleNotFoundException extends ApiException {

    public DefaultRoleNotFoundException(String roleName, MessageService messageService) {
        super(
                HttpStatus.NOT_FOUND,
                messageService.getMessage("default.role.not.found.exception", roleName)
        );
    }
}
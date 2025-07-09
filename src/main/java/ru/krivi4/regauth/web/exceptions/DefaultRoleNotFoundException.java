package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.DefaultMessageService;

/**
 * Исключение, бросаемое при отсутствии в БД роли с указанным именем.
 * HTTP 404 Not Found.
 */
public class DefaultRoleNotFoundException extends ApiException {

    private static final String MSG_KEY = "default.role.not.found.exception";

    public DefaultRoleNotFoundException(String roleName, DefaultMessageService ms) {
        super(HttpStatus.NOT_FOUND, ms.getMessage(MSG_KEY, roleName));
    }
}

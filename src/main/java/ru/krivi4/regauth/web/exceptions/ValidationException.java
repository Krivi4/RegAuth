package ru.krivi4.regauth.web.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.DefaultMessageService;

import java.util.Map;

/**
 * Ошибка валидации входящих данных.
 * HTTP 400 Bad Request.
 */
@Getter
public class ValidationException extends ApiException {

    private static final String MSG_KEY = "validation.exception";

    private final Map<String, String> errors;

    public ValidationException(Map<String, String> errors, DefaultMessageService ms) {
        super(HttpStatus.BAD_REQUEST, ms.getMessage(MSG_KEY));
        this.errors = errors;
    }
}

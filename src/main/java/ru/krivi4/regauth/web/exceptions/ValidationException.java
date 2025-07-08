package ru.krivi4.regauth.web.exceptions;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

import java.util.Map;

/**Ошибка валидации входящих данных (HTTP 400).*/
@Getter
public class ValidationException extends ApiException {

  private final Map<String, String> errors;

  public ValidationException(Map<String, String> errors, MessageService messageService) {

    super(HttpStatus.BAD_REQUEST, messageService.getMessage("validation.exception"));
    this.errors = errors;
  }
}
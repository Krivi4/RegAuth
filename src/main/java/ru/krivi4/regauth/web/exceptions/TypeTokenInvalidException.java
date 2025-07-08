package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**Неверный тип токена (HTTP 400).*/
public class TypeTokenInvalidException extends ApiException {

  public TypeTokenInvalidException(String phaseToken, MessageService messageService) {
    super(HttpStatus.BAD_REQUEST, messageService.getMessage("type.token.invalid.exception", phaseToken));
  }
}

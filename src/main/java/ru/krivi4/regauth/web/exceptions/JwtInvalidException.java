package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**Неверный или просроченный JWT (HTTP 400).*/
public class JwtInvalidException extends ApiException {

  public JwtInvalidException(MessageService messageService) {
    super(HttpStatus.BAD_REQUEST, messageService.getMessage("jwt.invalid.exception"));
  }
}

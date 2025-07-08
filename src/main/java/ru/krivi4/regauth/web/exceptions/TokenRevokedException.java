package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**Токен был отозван (HTTP 401).*/
public class TokenRevokedException extends ApiException {

  public TokenRevokedException(MessageService messageService) {
    super(HttpStatus.UNAUTHORIZED, messageService.getMessage("token.revoked.exception"));
  }
}

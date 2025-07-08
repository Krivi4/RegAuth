package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**Неизвестная фаза токена (HTTP 400).*/
public class PhaseUnknownException extends ApiException {

  public PhaseUnknownException(String rawPhase, MessageService messageService) {
    super(HttpStatus.BAD_REQUEST, messageService.getMessage("phase.unknown.exception") + rawPhase);
  }
}

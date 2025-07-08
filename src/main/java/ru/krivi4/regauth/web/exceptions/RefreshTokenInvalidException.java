package ru.krivi4.regauth.web.exceptions;


import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**Refresh-токен недействителен (HTTP 401).*/
public class RefreshTokenInvalidException extends ApiException {

  public RefreshTokenInvalidException(MessageService messageService) {
    super(HttpStatus.UNAUTHORIZED, messageService.getMessage("refresh.token.revoked.exception"));
  }
}

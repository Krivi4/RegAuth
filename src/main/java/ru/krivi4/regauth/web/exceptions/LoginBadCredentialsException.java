package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**Неверные учётные данные при логине (HTTP 401).*/
public class LoginBadCredentialsException extends ApiException {

  public LoginBadCredentialsException(MessageService messageService) {
    super(HttpStatus.UNAUTHORIZED, messageService.getMessage("login.bad.credentials.exception"));
  }
}

package ru.krivi4.regauth.web.exceptions;


import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**Неверный или просроченный Otp (HTTP 401).*/
public class OtpTokenInvalidException extends ApiException {

  public OtpTokenInvalidException(MessageService messageService) {
    super(HttpStatus.UNAUTHORIZED, messageService.getMessage("otp.token.invalid.exception"));
  }
}

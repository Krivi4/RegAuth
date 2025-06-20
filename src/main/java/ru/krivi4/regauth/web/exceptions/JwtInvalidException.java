package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;

/**Неверный или просроченный JWT (HTTP 400).*/
public class JwtInvalidException extends ApiException {

  public JwtInvalidException() {
    super(HttpStatus.BAD_REQUEST, "Недопустимый токен JWT");
  }
}

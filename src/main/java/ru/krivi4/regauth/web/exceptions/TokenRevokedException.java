package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
/**Токен был отозван (HTTP 401).*/
public class TokenRevokedException extends ApiException {

  public TokenRevokedException() {
    super(HttpStatus.UNAUTHORIZED, "Токен был отозван");
  }
}

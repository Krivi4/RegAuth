package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;

/**Неизвестная фаза токена (HTTP 400).*/
public class PhaseUnknownException extends ApiException {

  public PhaseUnknownException(String rawPhase) {
    super(HttpStatus.BAD_REQUEST, "Неизвестная Jwt phase: " + rawPhase);
  }
}

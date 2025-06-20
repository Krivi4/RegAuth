package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
/**Неверный тип токена (HTTP 400).*/
public class TypeTokenInvalidException extends ApiException {

  public TypeTokenInvalidException(String phaseToken) {
    super(HttpStatus.BAD_REQUEST, "Неверный тип токена, требуется " + phaseToken + " токен");
  }
}

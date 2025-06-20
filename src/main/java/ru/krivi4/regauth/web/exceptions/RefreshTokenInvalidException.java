package ru.krivi4.regauth.web.exceptions;


import org.springframework.http.HttpStatus;
/**Refresh-токен недействителен (HTTP 401).*/
public class RefreshTokenInvalidException extends ApiException {

  public RefreshTokenInvalidException() {
    super(HttpStatus.UNAUTHORIZED, "Refresh-токен неактивен или истёк");
  }
}

package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;

/**Неверные учётные данные при логине (HTTP 401).*/
public class LoginBadCredentialsException extends ApiException {

  public LoginBadCredentialsException() {
    super(HttpStatus.UNAUTHORIZED, "Неверное имя пользователя или пароль!");
  }
}

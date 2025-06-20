package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;

/**Нарушение уникальности пользователя (HTTP 409).*/
public class DuplicatePersonException extends ApiException {

  public DuplicatePersonException(String username) {
    super(HttpStatus.CONFLICT, "Пользователь '" + username + "' уже зарегистрирован");
    }
}

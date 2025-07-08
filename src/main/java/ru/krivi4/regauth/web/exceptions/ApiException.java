package ru.krivi4.regauth.web.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Базовый класс HTTP-ориентированных исключений приложения.
 * Содержит HttpStatus и текст сообщения для клиента.
 */
@Getter
public abstract class ApiException extends RuntimeException {

  private final HttpStatus status;
  /**Создаёт исключение с HTTP-статусом и сообщением.*/
  public ApiException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }
/**
 * Создаёт исключение с HTTP-статусом, сообщением и причиной ошибки.
 */
  public ApiException(HttpStatus status, String message, Throwable cause) {
    super(message, cause);
    this.status = status;
  }

}
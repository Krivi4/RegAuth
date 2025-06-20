package ru.krivi4.regauth.web.exceptions;


import org.springframework.http.HttpStatus;

/**Неверный или просроченный Otp (HTTP 401).*/
public class OtpTokenInvalidException extends ApiException {

  public OtpTokenInvalidException() {
    super(HttpStatus.UNAUTHORIZED, "Неверный или истёкший код подтверждения (Otp)");
  }
}

package ru.krivi4.regauth.web.exceptions;

/**Фаза JWT равна OTP_PENDING, поэтому аутентификация должна быть пропущена.*/
public class AuthenticateSkipException extends RuntimeException {

  public AuthenticateSkipException() {
    super("Phase OTP_PENDING → пропустить аутентификацию");
    }
}

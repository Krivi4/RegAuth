package ru.krivi4.regauth.jwt.handler;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.jwt.Phase;
import ru.krivi4.regauth.web.exceptions.AuthenticateSkipException;

/**Обработчик фазы OTP_PENDING: пропускает аутентификацию до ввода Otp.*/
@Component
public class OtpPendingHandler implements JwtPhaseHandler {

  /** Возвращает фазу Otp-PENDING. */
  @Override public Phase phase() {
    return Phase.OTP_PENDING;
  }

  /** Пропускаем аутентификацию и продолжаем обработку запроса*/
  @Override
  public Authentication handle(DecodedJWT jwt) {
    throw new AuthenticateSkipException();
  }
}
package ru.krivi4.regauth.jwt.handler;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import ru.krivi4.regauth.jwt.Phase;

/**Интерфейс для обработчиков JWT-фаз (FULL, OTP_PENDING, REFRESH).*/
public interface JwtPhaseHandler {

  /**Фаза, которую обрабатывает реализация*/
  Phase phase();

  /**Обрабатывает декодированный JWT и возвращает Authentication.*/
  Authentication handle(DecodedJWT jwt);
}
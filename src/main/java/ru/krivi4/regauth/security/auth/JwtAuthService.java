package ru.krivi4.regauth.security.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.krivi4.regauth.jwt.Phase;
import ru.krivi4.regauth.jwt.handler.JwtPhaseHandler;
import ru.krivi4.regauth.jwt.util.JwtUtil;
import ru.krivi4.regauth.web.exceptions.JwtInvalidException;
import ru.krivi4.regauth.web.exceptions.PhaseUnknownException;

import java.util.Map;

/**Сервис аутентификации JWT-токенов.*/
@Service
@RequiredArgsConstructor
public class JwtAuthService {

  private final JwtUtil jwtUtil;
  private final Map<Phase, JwtPhaseHandler> handlers;

  /**Проверяет валидность JWT и возвращает готовый объект Authentication*/
  public Authentication authenticate(String rawJwt) {

    DecodedJWT decodedJWT;

    try {
      decodedJWT = jwtUtil.decode(rawJwt);
    } catch (JWTVerificationException e) {
      throw new JwtInvalidException();
    }

    Phase phase = Phase.fromClaim(decodedJWT.getClaim("phase").asString());
    JwtPhaseHandler handler = handlers.get(phase);
    if (handler == null) {
      throw new PhaseUnknownException(phase.name());
    }
    return handler.handle(decodedJWT);
  }
}
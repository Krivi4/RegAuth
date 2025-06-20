package ru.krivi4.regauth.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.services.tokens.JwtBlackListService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * Обработчик успешного выхода (logout).
 * Извлекает JTI и время истечения из токена,
 * сохраняет их в blacklist через JwtBlackListService>
 */
@Component
@RequiredArgsConstructor
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

  private final JwtBlackListService jwtBlackListService;

  /**
   * Срабатывает при успешном logout и выполняет отзыв токена:
   * извлекает JTI и время истечения из заголовка Authorization,
   * сохраняет их в blacklist и возвращает HTTP 200 с подтверждением.
   */
  @Override
  public void onLogoutSuccess(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException {

    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      String jwtToken = header.substring(7);
      DecodedJWT jwt = JWT.decode(jwtToken);
      UUID jti = UUID.fromString(jwt.getId());
      Instant expiresAt = jwt.getExpiresAt().toInstant();
      jwtBlackListService.block(jti, expiresAt);
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write("{\"message\":\"Logged out\"}");
    }
  }
}

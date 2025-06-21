package ru.krivi4.regauth.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.services.tokens.AccessTokenBlackListService;
import ru.krivi4.regauth.services.tokens.RefreshTokenBlackListService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * Logout-обработчик:
 * блокирует access-токен и отзывает refresh-токен.
 */
@Component
@RequiredArgsConstructor
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

  private final AccessTokenBlackListService accessTokenBlackListService;
  private final RefreshTokenBlackListService refreshTokenBlackListService;

  private static final String REFRESH_HEADER = "X-Refresh-Token";

  /**
   * Блокирует access-токен и отзывает refresh-токен,
   * после чего возвращает HTTP 200 и JSON { "message":"Logged out" }.
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
      UUID accessJti = UUID.fromString(jwt.getId());
      Instant expiresAt = jwt.getExpiresAt().toInstant();
      accessTokenBlackListService.block(accessJti, expiresAt);

      String refreshHeader = request.getHeader(REFRESH_HEADER); // ¹
      if (refreshHeader != null && refreshHeader.startsWith("Bearer ")) {
        String refreshToken = refreshHeader.substring(7).trim();
        DecodedJWT rJwt = JWT.decode(refreshToken);
        UUID refreshJti = UUID.fromString(rJwt.getId());

        refreshTokenBlackListService.revoke(refreshJti);
      }

      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write("{\"message\":\"Logged out\"}");
    }
  }
}

package ru.krivi4.regauth.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.services.message.MessageService;
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
  private final MessageService messageService;

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String REFRESH_HEADER       = "X-Refresh-Token";
  private static final String BEARER_PREFIX        = "Bearer ";

  /**
   * Основной точка входа при logout.
   * Если в запросе есть корректный Bearer-заголовок,
   * выполняет процесс блокировки токенов и пишет ответ клиенту.
   */
  @Override
  public void onLogoutSuccess(
          HttpServletRequest request,
          HttpServletResponse response,
          Authentication authentication
  ) throws IOException {
    if (!isBearerHeaderPresent(request)) return;

    handleLogout(request);
    writeSuccessResponse(response);
  }

  /**
   * Выполняет всю логику «отзыва» токенов:
   * извлекает access-токен, блокирует его в черном списке,
   * затем при наличии refresh-токена — отзывает и его.
   */
  private void handleLogout(HttpServletRequest request) {
    String accessToken = extractToken(request.getHeader(AUTHORIZATION_HEADER));
    processAccessToken(accessToken);

    String refreshHeader = request.getHeader(REFRESH_HEADER);
    if (isBearerValue(refreshHeader)) {
      String refreshToken = extractToken(refreshHeader);
      processRefreshToken(refreshToken);
    }
  }

  private boolean isBearerHeaderPresent(HttpServletRequest request) {
    String header = request.getHeader(AUTHORIZATION_HEADER);
    return isBearerValue(header);
  }

  private boolean isBearerValue(String headerValue) {
    return headerValue != null && headerValue.startsWith(BEARER_PREFIX);
  }

  private String extractToken(String headerValue) {
    return headerValue.substring(BEARER_PREFIX.length()).trim();
  }

  /**
   * Декодирует переданный access-JWT, извлекает его jti и expiresAt,
   * передаёт их в AccessTokenBlackListService для блокировки.
   */
  private void processAccessToken(String jwtToken) {
    DecodedJWT decoded = JWT.decode(jwtToken);
    UUID tokenId = UUID.fromString(decoded.getId());
    Instant expiry = decoded.getExpiresAt().toInstant();
    accessTokenBlackListService.block(tokenId, expiry);
  }

  /**
   * Декодирует переданный refresh-JWT, извлекает его jti
   * и передаёт его в RefreshTokenBlackListService для отзыва.
   */
  private void processRefreshToken(String jwtToken) {
    DecodedJWT decoded = JWT.decode(jwtToken);
    UUID tokenId = UUID.fromString(decoded.getId());
    refreshTokenBlackListService.revoke(tokenId);
  }

  /**
   * Формирует HTTP-ответ с кодом 200 и телом
   * где сообщение берётся из messages.properties.
   */
  private void writeSuccessResponse(HttpServletResponse response) throws IOException {
    String message = messageService.getMessage("logout.success.message");
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write("{\"message\":\"" + message + "\"}");
  }
}

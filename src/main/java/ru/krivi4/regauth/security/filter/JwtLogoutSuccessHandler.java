package ru.krivi4.regauth.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.services.tokens.AccessTokenBlacklistService;
import ru.krivi4.regauth.services.tokens.RefreshTokenBlacklistService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * Logout‑обработчик:
 * блокирует access‑токен и отзывает refresh‑токен.
 */
@Component
@RequiredArgsConstructor
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_REFRESH = "X-Refresh-Token";
    private static final String TOKEN_PREFIX_BEARER = "Bearer ";
    private static final String MESSAGE_LOGOUT_SUCCESS = "logout.success.message";
    private static final String RESPONSE_CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
    private static final String JSON_MESSAGE_TEMPLATE = "{\"message\":\"%s\"}";

    private final AccessTokenBlacklistService accessTokenBlackListService;
    private final RefreshTokenBlacklistService refreshTokenBlackListService;
    private final MessageService messageService;

    /**
     * Точка входа при logout.
     * Если в запросе есть корректный Bearer‑заголовок,
     * выполняет блокировку access‑токена и отзывается refresh‑токен.
     * Возвращает клиенту успешный JSON‑ответ.
     */
    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        if (!isBearerHeaderPresent(request)) {
            return;
        }
        handleLogout(request);
        writeSuccessResponse(response);
    }

    /**
     * Выполняет логику отзыва токенов:
     * извлекает access‑токен, блокирует его в черном списке,
     * при наличии refresh‑токена отзывает и его.
     */
    private void handleLogout(HttpServletRequest request) {
        String accessToken = extractToken(request.getHeader(HEADER_AUTHORIZATION));
        processAccessToken(accessToken);

        String refreshHeader = request.getHeader(HEADER_REFRESH);
        if (isBearerValue(refreshHeader)) {
            String refreshToken = extractToken(refreshHeader);
            processRefreshToken(refreshToken);
        }
    }

    /**
     * Проверяет наличие корректного Bearer‑заголовка Authorization.
     */
    private boolean isBearerHeaderPresent(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        return isBearerValue(header);
    }

    /**
     * Проверяет, начинается ли строка заголовка с «Bearer ».
     */
    private boolean isBearerValue(String headerValue) {
        return headerValue != null && headerValue.startsWith(TOKEN_PREFIX_BEARER);
    }

    /**
     * Извлекает токен из заголовка, обрезая префикс «Bearer ».
     */
    private String extractToken(String headerValue) {
        return headerValue.substring(TOKEN_PREFIX_BEARER.length()).trim();
    }

    /**
     * Декодирует access‑токен, извлекает его jti и время истечения,
     * затем передаёт их в DefaultAccessTokenBlackListService для блокировки.
     */
    private void processAccessToken(String jwtToken) {
        DecodedJWT decoded = JWT.decode(jwtToken);
        UUID tokenId = UUID.fromString(decoded.getId());
        Instant expiry = decoded.getExpiresAt().toInstant();
        accessTokenBlackListService.block(tokenId, expiry);
    }

    /**
     * Декодирует refresh‑токен, извлекает его jti
     * и передаёт в DefaultRefreshTokenBlackListService для отзыва.
     */
    private void processRefreshToken(String jwtToken) {
        DecodedJWT decoded = JWT.decode(jwtToken);
        UUID tokenId = UUID.fromString(decoded.getId());
        refreshTokenBlackListService.revoke(tokenId);
    }

    /**
     * Формирует JSON‑ответ со статусом 200 и сообщением об успешном logout.
     */
    private void writeSuccessResponse(HttpServletResponse response) throws IOException {
        String message = messageService.getMessage(MESSAGE_LOGOUT_SUCCESS);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(RESPONSE_CONTENT_TYPE_JSON);
        response.getWriter().write(String.format(JSON_MESSAGE_TEMPLATE, message));
    }
}

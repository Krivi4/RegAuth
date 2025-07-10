package ru.krivi4.regauth.security.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.krivi4.regauth.security.auth.JwtAuthService;
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.web.exceptions.ApiException;
import ru.krivi4.regauth.web.exceptions.AuthenticateSkipException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Фильтр, который проверяет JWT-токен ровно один раз за HTTP-запрос.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String MESSAGE_JWT_INVALID_HEADER = "jwt.invalid.header.exception";

    private final JwtAuthService jwtAuthService;
    private final MessageService messageService;

    /**
     * Проверяет JWT-токен и выполняет аутентификацию.
     * Если токен отсутствует или некорректен — продолжает цепочку фильтров без изменений.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (shouldSkipAuthentication(request)) {
            proceedNextFilter(request, response, filterChain);
            return;
        }

        if (handleEmptyTokenIfPresent(request, response)) {
            return;
        }

        handleAuthenticationFlow(request, response, filterChain);
    }

    //*----------Вспомогательные методы----------*//

    /**
     * Проверяет, нужно ли пропустить аутентификацию,
     * если заголовок Authorization отсутствует или не начинается с Bearer.
     */
    private boolean shouldSkipAuthentication(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        return header == null || !header.startsWith(BEARER_PREFIX);
    }

    /**
     * Проверяет наличие пустого токена и отправляет 400 Bad Request, если он пустой.
     * Возвращает true, если обработка запроса завершена ошибкой.
     */
    private boolean handleEmptyTokenIfPresent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String jwtToken = extractJwtToken(request);
        if (jwtToken.isBlank()) {
            String message = messageService.getMessage(MESSAGE_JWT_INVALID_HEADER);
            sendClientError(response, HttpServletResponse.SC_BAD_REQUEST, message);
            return true;
        }
        return false;
    }

    /**
     * Выполняет полную обработку JWT: аутентификацию, установку контекста
     * и продолжение цепочки фильтров. Обрабатывает исключения ApiException и AuthenticateSkipException.
     */
    private void handleAuthenticationFlow(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String jwtToken = extractJwtToken(request);
        try {
            authenticateAndSetContext(jwtToken);
            proceedNextFilter(request, response, filterChain);

        } catch (AuthenticateSkipException e) {
            proceedNextFilter(request, response, filterChain);

        } catch (ApiException e) {
            sendClientError(response, e.getStatus().value(), e.getMessage());
        }
    }

    /**
     * Аутентифицирует JWT-токен и сохраняет Authentication в SecurityContext.
     */
    private void authenticateAndSetContext(String jwtToken) {
        Authentication authentication = jwtAuthService.authenticate(jwtToken);
        setAuthenticationIfAbsent(authentication);
    }

    /**
     * Сохраняет Authentication в SecurityContext, если он ещё не установлен.
     */
    private void setAuthenticationIfAbsent(Authentication authentication) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    /**
     * Извлекает токен из заголовка Authorization без префикса Bearer.
     */
    private String extractJwtToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        return header.substring(BEARER_PREFIX.length()).trim();
    }

    /**
     * Продолжает выполнение цепочки фильтров без дополнительной обработки.
     */
    private void proceedNextFilter(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }

    /**
     * Отправляет клиенту HTTP-ответ с кодом ошибки и текстом сообщения.
     */
    private void sendClientError(
            HttpServletResponse response,
            int statusCode,
            String message
    ) throws IOException {
        response.sendError(statusCode, message);
    }
}

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

    private final JwtAuthService jwtAuthService;
    private final MessageService messageService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";


    /**
     * Проверяет заголовок Authorization:
     * - если отсутствует или не «Bearer» → просто продолжает цепочку фильтров;
     * - иначе передаёт управление в handleAuthentication().
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (headerIsMissingOrNotBearer(request)) {
            proceedNext(request, response, filterChain);
            return;
        }

        handleAuthentication(request, response, filterChain);
    }

    /**
     * Извлекает и проверяет «сырую» часть JWT:
     * - если пустая → возвращает 400 Bad Request с сообщением из bundle;
     * - иначе передаёт токен на аутентификацию и продолжает цепочку.
     */
    private void handleAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String rawToken = extractToken(request);

        if (rawToken.isBlank()) {
            sendClientError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    messageService.getMessage("jwt.invalid.header.exception")
            );
            return;
        }
        tryAuthenticateAndContinue(rawToken, request, response, filterChain);
    }

    /**
     * Проверка, что заголовок Authorization существует и начинается с «Bearer ».
     */
    private boolean headerIsMissingOrNotBearer(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        return header == null || !header.startsWith(BEARER_PREFIX);
    }

    /**
     * Переход к следующему фильтру без какой-либо обработки.
     */
    private void proceedNext(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }

    /**
     * Извлекает строку с токеном, обрезая префикс «Bearer ».
     */
    private String extractToken(HttpServletRequest request) {
        return request.getHeader("Authorization").substring(BEARER_PREFIX.length()).trim();
    }

    private void tryAuthenticateAndContinue(
            String jwtToken,
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            Authentication authentication = jwtAuthService.authenticate(jwtToken);
            putIntoSecurityContextIfAbsent(authentication);
            proceedNext(request, response, filterChain);

        } catch (AuthenticateSkipException skipException) {
            proceedNext(request, response, filterChain);

        } catch (ApiException apiException) {
            sendClientError(response,
                    apiException.getStatus().value(),
                    apiException.getMessage());
        }
    }

    /**
     * Сохраняет Authentication в контекст, если там ещё ничего нет.
     */
    private void putIntoSecurityContextIfAbsent(Authentication authentication) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    /**
     * Универсальная отправка HTTP-ошибки клиенту.
     */
    private void sendClientError(
            HttpServletResponse response,
            int statusCode,
            String message
    ) throws IOException {
        response.sendError(statusCode, message);
    }
}

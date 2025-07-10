package ru.krivi4.regauth.security.auth;

import org.springframework.security.core.Authentication;

/**
 * Сервис для проверки JWT токена и получения данных аутентификации.
 */
public interface JwtAuthService {

    /**
     * Проверяет JWT токен и возвращает объект аутентификации.
     */
    Authentication authenticate(String rawJwt);
}

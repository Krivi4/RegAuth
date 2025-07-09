package ru.krivi4.regauth.security.auth;

import org.springframework.security.core.Authentication;

/**
 * Контракт для сервиса аутентификации JWT‑токенов.
 */
public interface JwtAuthService {

    Authentication authenticate(String rawJwt);
}

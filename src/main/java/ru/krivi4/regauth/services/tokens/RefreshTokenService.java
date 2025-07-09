package ru.krivi4.regauth.services.tokens;

import ru.krivi4.regauth.models.RefreshToken;

/**
 * Интерфейс сервиса управления refresh‑токенами.
 */
public interface RefreshTokenService {

    void save(String refreshToken);

    void revoked(RefreshToken refreshToken);

    RefreshToken validate(String rawJwt);

    void purgeExpired();
}

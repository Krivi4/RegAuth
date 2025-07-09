package ru.krivi4.regauth.services.tokens;

import java.time.Instant;
import java.util.UUID;

/**
 * Интерфейс сервиса управления чёрным списком refresh‑токенов.
 */
public interface RefreshTokenBlacklistService {

    void revoke(UUID jti);

    boolean isRevoked(UUID jti);

    void cleanExpired();
}

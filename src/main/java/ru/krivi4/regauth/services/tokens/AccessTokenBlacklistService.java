package ru.krivi4.regauth.services.tokens;

import java.time.Instant;
import java.util.UUID;

/**
 * Интерфейс сервиса управления чёрным списком access‑токенов.
 */
public interface AccessTokenBlacklistService {

    void block(UUID jti, Instant expiresAt);

    boolean isBlocked(UUID jti);

    void cleanExpired();
}
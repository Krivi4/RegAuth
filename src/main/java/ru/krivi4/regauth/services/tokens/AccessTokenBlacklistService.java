package ru.krivi4.regauth.services.tokens;

import java.time.Instant;
import java.util.UUID;

/**
 * Сервис для работы с чёрным списком access‑токенов.
 */
public interface AccessTokenBlacklistService {

    /**
     * Добавляет access‑токен в чёрный список до указанного времени истечения.
     */
    void block(UUID jti, Instant expiresAt);

    /**
     * Проверяет, находится ли access‑токен в чёрном списке.
     */
    boolean isBlocked(UUID jti);

    /**
     * Удаляет просроченные токены из чёрного списка.
     */
    void cleanExpired();
}

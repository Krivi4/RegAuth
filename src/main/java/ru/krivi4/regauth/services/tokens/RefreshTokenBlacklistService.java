package ru.krivi4.regauth.services.tokens;

import java.util.UUID;

/**
 * Сервис для работы с чёрным списком refresh‑токенов.
 */
public interface RefreshTokenBlacklistService {

    /**
     * Добавляет refresh‑токен в чёрный список (отзывает его).
     */
    void revoke(UUID jti);

    /**
     * Проверяет, отозван ли refresh‑токен.
     */
    boolean isRevoked(UUID jti);

    /**
     * Удаляет просроченные токены из чёрного списка.
     */
    void cleanExpired();
}

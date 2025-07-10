package ru.krivi4.regauth.services.tokens;

import ru.krivi4.regauth.models.RefreshToken;

/**
 * Сервис для управления refresh‑токенами.
 */
public interface RefreshTokenService {

    /**
     * Сохраняет новый refresh‑токен в базе.
     */
    void save(String refreshToken);

    /**
     * Отзывает refresh‑токен и помечает его как недействительный.
     */
    void revoked(RefreshToken refreshToken);

    /**
     * Проверяет refresh‑токен и возвращает его сущность.
     */
    RefreshToken validate(String rawJwt);

    /**
     * Удаляет просроченные refresh‑токены из базы.
     */
    void purgeExpired();
}

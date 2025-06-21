package ru.krivi4.regauth.services.tokens;

import java.time.Instant;
import java.util.UUID;

/** Чёрный список access-токенов. */
public interface AccessTokenBlacklist {
  /**
   * Блокирует access-токен:
   * сохраняет JTI и срок жизни в хранилище.
   */
  void block(UUID jti, Instant expiresAt);

  /** Проверяет, заблокирован ли access-токен. */
  boolean isBlocked(UUID jti);

  /** Очищает просроченные записи. */
  void cleanExpired();
}
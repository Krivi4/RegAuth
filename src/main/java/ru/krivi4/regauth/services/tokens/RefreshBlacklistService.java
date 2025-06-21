package ru.krivi4.regauth.services.tokens;

import java.time.Instant;
import java.util.UUID;

/** Чёрный список refresh-токенов. */
public interface RefreshBlacklistService {

  /** Отзывает refresh-токен по JTI. */
  void revoke(UUID jti);

  /** Проверка, был ли токен отозван. */
  boolean isRevoked(UUID jti);

  /** Очищает просроченные записи. */
  void cleanExpired();
}

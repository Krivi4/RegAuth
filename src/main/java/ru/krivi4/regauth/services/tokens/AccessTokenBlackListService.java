package ru.krivi4.regauth.services.tokens;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.models.RevokedAccessToken;
import ru.krivi4.regauth.repositories.RevokedAccessTokenRepository;

import java.time.*;
import java.util.UUID;

/**Чёрный список access‑токенов: блокировка JTI и удаление просроченных записей.*/
@Service
@RequiredArgsConstructor
@Slf4j
public class AccessTokenBlackListService implements AccessTokenBlacklist{

  private final RevokedAccessTokenRepository revokedAccessTokenRepository;

  /**Блокирует JTI до истечения срока действия токена.*/
  @Transactional
  @Override
  public void block(UUID jti, Instant expiresInstant) {
    LocalDateTime expiresAt =
      LocalDateTime.ofInstant(expiresInstant, ZoneId.of("Europe/Moscow"));
    revokedAccessTokenRepository.save(new RevokedAccessToken(jti, expiresAt));
  }

  /**Проверяет, заблокирован ли JTI.*/
  @Transactional(readOnly = true)
  @Override
  public  boolean isBlocked(UUID jti) {
    return revokedAccessTokenRepository.existsById(jti);
  }

  /**Ежедневно очищает просроченные записи.*/
  @Transactional
  @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Moscow")
  @Override
  public void cleanExpired() {
    long removed =
      revokedAccessTokenRepository.deleteByExpiresAtBefore(
        LocalDateTime.now(ZoneId.of("Europe/Moscow"))
      );
    log.info("Очищенные {} аннулированные токены с истекшим сроком действия", removed);
  }
}

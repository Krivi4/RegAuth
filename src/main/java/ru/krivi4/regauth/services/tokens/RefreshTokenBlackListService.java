package ru.krivi4.regauth.services.tokens;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.models.RefreshToken;
import ru.krivi4.regauth.repositories.RefreshTokenRepository;

import java.time.*;
import java.util.UUID;

/**
 * «Чёрный список» refresh-токенов:
 * переводит флаг revoked=true и удаляет устаревшие записи.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenBlackListService implements RefreshBlacklistService {

  private final RefreshTokenRepository refreshTokenRepository;

  /** Помечает refresh-токен отозванным. */
  @Transactional
  @Override
  public void revoke(UUID jti) {
    refreshTokenRepository.findById(jti).ifPresent(refreshToken -> {
      refreshToken.setRevoked(true);
      refreshTokenRepository.save(refreshToken);
    });
  }

  /**Проверяет, отозван ли токен.*/
  @Transactional(readOnly = true)
  public  boolean isRevoked(UUID jti) {
    return refreshTokenRepository.findById(jti)
      .map(RefreshToken::isRevoked)
      .orElse(false);
  }

  /**Ежедневно удаляет просроченные refresh-токены.*/
  @Transactional
  @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Moscow")
  @Override
  public void cleanExpired() {
    long removed =
      refreshTokenRepository.deleteByExpiresAtBefore(
        LocalDateTime.now(ZoneId.of("Europe/Moscow"))
      );
    log.info("Очищенные {} аннулированные токены с истекшим сроком действия", removed);
  }
}

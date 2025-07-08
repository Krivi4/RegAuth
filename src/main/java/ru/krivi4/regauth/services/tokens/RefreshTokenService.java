package ru.krivi4.regauth.services.tokens;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.jwt.util.JwtUtil;
import ru.krivi4.regauth.models.RefreshToken;
import ru.krivi4.regauth.repositories.RefreshTokenRepository;
import ru.krivi4.regauth.services.message.MessageService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

/**Хранит, валидирует и отзывает refresh‑токены.*/
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final JwtUtil jwtUtil;
  private final RefreshTokenRepository refreshTokenRepository;
  private final MessageService messageService;

  private static final String MOSCOW_ZONE         = "Europe/Moscow";
  private static final String USERNAME_CLAIM      = "username";
  private static final String HOURLY_CLEANUP_CRON = "0 0 * * * *";

  /**
   * Сохраняет новый refresh‑токен в базе.
   */
  @Transactional
  public void save(String refreshToken) {
    RefreshToken tokenEntity = buildTokenEntity(refreshToken);
    refreshTokenRepository.save(tokenEntity);
  }

  /**
   * Помечает refresh‑токен как отозванный.
   */
  @Transactional
  public void revoked(RefreshToken refreshToken) {
    refreshToken.setRevoked(true);
    refreshTokenRepository.save(refreshToken);
  }

  /**
   * Проверяет валидность refresh‑токена и возвращает его сущность.
   */
  @Transactional(readOnly = true)
  public RefreshToken validate(String rawJwt) {
    DecodedJWT decodedJwt = jwtUtil.decode(rawJwt);
    UUID jti = UUID.fromString(decodedJwt.getId());

    RefreshToken token = findActiveTokenOrThrow(jti);
    rejectIfExpired(token);

    return token;
  }

  /**
   * Удаляет все просроченные refresh‑токены. Запускается каждый час.
   */
  @Scheduled(cron = HOURLY_CLEANUP_CRON, zone = MOSCOW_ZONE)
  @Transactional
  public void purgeExpired() {
    refreshTokenRepository.deleteByExpiresAtBefore(
            LocalDateTime.now(ZoneId.of(MOSCOW_ZONE))
    );
  }

  /* ---------------Вспомогательные методы--------------- */

  /**
   * Строит объект RefreshToken из строкового токена.
   */
  private RefreshToken buildTokenEntity(String refreshToken) {
    DecodedJWT decodedJwt = jwtUtil.decode(refreshToken);

    UUID jti = UUID.fromString(decodedJwt.getId());
    String username = decodedJwt.getClaim(USERNAME_CLAIM).asString();
    LocalDateTime expiresAt = LocalDateTime.ofInstant(
            decodedJwt.getExpiresAt().toInstant(),
            ZoneId.of(MOSCOW_ZONE)
    );

    RefreshToken token = new RefreshToken();
    token.setJti(jti);
    token.setUsername(username);
    token.setExpiresAt(expiresAt);
    token.setRevoked(false);

    return token;
  }

  /**
   * Находит активный refresh‑токен по jti или выбрасывает исключение.
   */
  private RefreshToken findActiveTokenOrThrow(UUID jti) {
    Optional<RefreshToken> maybeToken = refreshTokenRepository.findByJtiAndRevokedFalse(jti);

    if (maybeToken.isEmpty()) {
      throw new IllegalArgumentException(messageService.getMessage("refresh.token.invalid.exception"));
    }

    return maybeToken.get();
  }

  /**
   * Проверяет срок действия токена и выбрасывает исключение, если он просрочен.
   */
  private void rejectIfExpired(RefreshToken token) {
    if (token.getExpiresAt().isBefore(LocalDateTime.now(ZoneId.of(MOSCOW_ZONE)))) {
      throw new IllegalArgumentException(messageService.getMessage("refresh.token.invalid.exception"));
    }
  }
}

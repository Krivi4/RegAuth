package ru.krivi4.regauth.services.tokens;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.jwt.util.JwtUtil;
import ru.krivi4.regauth.models.RefreshToken;
import ru.krivi4.regauth.repositories.RefreshTokenRepository;

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

  /**Сохраняет новый refresh‑токен.*/
  @Transactional
  public void save(String refreshToken) {
    RefreshToken refreshTokenO = new RefreshToken();
    DecodedJWT decodedJWT = jwtUtil.decode(refreshToken);
    UUID jti = UUID.fromString(decodedJWT.getId());
    String username = decodedJWT.getClaim("username").asString();
    LocalDateTime expiresAt =
      LocalDateTime.ofInstant(
        decodedJWT.getExpiresAt().toInstant(),
        ZoneId.of("Europe/Moscow")
      );
    refreshTokenO.setJti(jti);
    refreshTokenO.setUsername(username);
    refreshTokenO.setExpiresAt(expiresAt);
    refreshTokenO.setRevoked(false);
    refreshTokenRepository.save(refreshTokenO);
  }

  /**Помечает токен отозванным.*/
  @Transactional
  public void revoked(RefreshToken refreshToken) {
    refreshToken.setRevoked(true);
    refreshTokenRepository.save(refreshToken);
  }

  /**Валидирует refresh‑токен и возвращает сущность.*/
  @Transactional(readOnly = true)
  public RefreshToken validate(String rawJwt) {
    DecodedJWT decodedJWT = jwtUtil.decode(rawJwt);
    UUID jti = UUID.fromString(decodedJWT.getId());

    Optional<RefreshToken> maybeToken =
      refreshTokenRepository.findByJtiAndRevokedFalse(jti);

    if (maybeToken.isEmpty()) {
      throw new IllegalArgumentException("Недействительный или отозванный Refresh токен");
    }

    RefreshToken token = maybeToken.get();
    LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
    if (token.getExpiresAt().isBefore(now)) {
      throw new IllegalArgumentException("Недействительный или отозванный Refresh токен");
    }
    return token;
  }

  /**Почасовая очистка просроченных refresh‑токенов.*/
  @Scheduled(cron = "0 0 * * * *", zone = "Europe/Moscow")
  @Transactional
  public void purgeExpired() {
    refreshTokenRepository.deleteByExpiresAtBefore(
      LocalDateTime.now(ZoneId.of("Europe/Moscow"))
    );
  }
}

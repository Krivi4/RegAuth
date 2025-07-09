package ru.krivi4.regauth.services.tokens;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.jwt.util.DefaultJwtUtil;
import ru.krivi4.regauth.models.RefreshToken;
import ru.krivi4.regauth.repositories.RefreshTokenRepository;
import ru.krivi4.regauth.services.message.DefaultMessageService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация сервиса управления refresh‑токенами
 */
@Service
@RequiredArgsConstructor
public class DefaultRefreshTokenService implements RefreshTokenService {

    private static final String MOSCOW_ZONE = "Europe/Moscow";
    private static final String USERNAME_CLAIM = "username";
    private static final String HOURLY_CLEANUP_CRON = "0 0 * * * *";
    private static final String INVALID_TOKEN_MSG_KEY = "refresh.token.invalid.exception";
    private static final boolean READ_ONLY = true;
    private static final boolean REVOKED_TRUE = true;
    private static final boolean REVOKED_FALSE = false;

    private final DefaultJwtUtil defaultJwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final DefaultMessageService defaultMessageService;

    @Override
    @Transactional
    public void save(String refreshToken) {
        RefreshToken tokenEntity = buildTokenEntity(refreshToken);
        refreshTokenRepository.save(tokenEntity);
    }

    @Override
    @Transactional
    public void revoked(RefreshToken refreshToken) {
        refreshToken.setRevoked(REVOKED_TRUE);
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = READ_ONLY)
    public RefreshToken validate(String rawJwt) {
        DecodedJWT decodedJwt = defaultJwtUtil.decode(rawJwt);
        UUID jti = UUID.fromString(decodedJwt.getId());

        RefreshToken token = findActiveTokenOrThrow(jti);
        rejectIfExpired(token);

        return token;
    }

    @Override
    @Transactional
    @Scheduled(cron = HOURLY_CLEANUP_CRON, zone = MOSCOW_ZONE)
    public void purgeExpired() {
        refreshTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now(ZoneId.of(MOSCOW_ZONE)));
    }

    /* -------------------- Вспомогательные методы -------------------- */
    private RefreshToken buildTokenEntity(String refreshToken) {
        DecodedJWT decodedJwt = defaultJwtUtil.decode(refreshToken);

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
        token.setRevoked(REVOKED_FALSE);

        return token;
    }

    private RefreshToken findActiveTokenOrThrow(UUID jti) {
        Optional<RefreshToken> maybeToken = refreshTokenRepository.findByJtiAndRevokedFalse(jti);

        if (maybeToken.isEmpty()) {
            throw new IllegalArgumentException(defaultMessageService.getMessage(INVALID_TOKEN_MSG_KEY));
        }
        return maybeToken.get();
    }

    private void rejectIfExpired(RefreshToken token) {
        if (token.getExpiresAt().isBefore(LocalDateTime.now(ZoneId.of(MOSCOW_ZONE)))) {
            throw new IllegalArgumentException(defaultMessageService.getMessage(INVALID_TOKEN_MSG_KEY));
        }
    }
}

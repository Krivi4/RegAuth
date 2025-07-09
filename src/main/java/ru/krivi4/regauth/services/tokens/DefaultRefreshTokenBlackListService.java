package ru.krivi4.regauth.services.tokens;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.models.RefreshToken;
import ru.krivi4.regauth.repositories.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * «Чёрный список» refresh‑токенов:
 * переводит флаг revoked=true и удаляет устаревшие записи.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultRefreshTokenBlackListService implements RefreshTokenBlacklistService {

    private static final String MOSCOW_TIME_ZONE = "Europe/Moscow";
    private static final String DAILY_CLEANUP_CRON = "0 0 0 * * *";
    private static final boolean READ_ONLY = true;
    private static final boolean REVOKED_TRUE = true;
    private static final boolean REVOKED_FALSE = false;
    private static final String CLEANUP_LOG_MESSAGE =
            "Очищены {} аннулированные refresh‑токены с истекшим сроком";

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Помечает refresh‑токен отозванным.
     */
    @Transactional
    @Override
    public void revoke(UUID jti) {
        refreshTokenRepository.findById(jti).ifPresent(refreshToken -> {
            refreshToken.setRevoked(REVOKED_TRUE);
            refreshTokenRepository.save(refreshToken);
        });
    }

    /**
     * Проверяет, отозван ли refresh‑токен.
     */
    @Transactional(readOnly = READ_ONLY)
    @Override
    public boolean isRevoked(UUID jti) {
        return refreshTokenRepository.findById(jti)
                .map(RefreshToken::isRevoked)
                .orElse(REVOKED_FALSE);
    }

    /**
     * Ежедневно удаляет просроченные refresh‑токены.
     */
    @Transactional
    @Scheduled(cron = DAILY_CLEANUP_CRON, zone = MOSCOW_TIME_ZONE)
    @Override
    public void cleanExpired() {
        long removed = refreshTokenRepository
                .deleteByExpiresAtBefore(LocalDateTime.now(ZoneId.of(MOSCOW_TIME_ZONE)));
        log.info(CLEANUP_LOG_MESSAGE, removed);
    }
}

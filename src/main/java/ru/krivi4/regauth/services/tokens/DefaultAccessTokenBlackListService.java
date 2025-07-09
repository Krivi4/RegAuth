package ru.krivi4.regauth.services.tokens;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.models.RevokedAccessToken;
import ru.krivi4.regauth.repositories.RevokedAccessTokenRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Instant;
import java.util.UUID;

/**
 * Реализация сервиса чёрного списка access‑токенов
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultAccessTokenBlackListService implements AccessTokenBlacklistService {

    private static final String MOSCOW_ZONE = "Europe/Moscow";
    private static final String DAILY_CLEANUP_CRON = "0 0 0 * * *";
    private static final String CLEANUP_LOG_MESSAGE = "Очищены {} аннулированные access‑токены с истекшим сроком";
    private static final boolean READ_ONLY = true;


    private final RevokedAccessTokenRepository revokedAccessTokenRepository;

    @Override
    @Transactional
    public void block(UUID jti, Instant expiresInstant) {
        LocalDateTime expiresAt = LocalDateTime.ofInstant(expiresInstant, ZoneId.of(MOSCOW_ZONE));
        revokedAccessTokenRepository.save(new RevokedAccessToken(jti, expiresAt));
    }

    @Override
    @Transactional(readOnly = READ_ONLY)
    public boolean isBlocked(UUID jti) {
        return revokedAccessTokenRepository.existsById(jti);
    }

    @Override
    @Transactional
    @Scheduled(cron = DAILY_CLEANUP_CRON, zone = MOSCOW_ZONE)
    public void cleanExpired() {
        long removed = revokedAccessTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now(ZoneId.of(MOSCOW_ZONE)));
        log.info(CLEANUP_LOG_MESSAGE, removed);
    }
}

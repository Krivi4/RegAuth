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
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для управления чёрным списком refresh‑токенов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultRefreshTokenBlackListService implements RefreshTokenBlacklistService {

    private static final String MOSCOW_ZONE = "Europe/Moscow";
    private static final String DAILY_CLEANUP_CRON = "0 0 0 * * *";
    private static final String CLEANUP_LOG_MESSAGE =
            "Очищены {} аннулированные refresh‑токены с истекшим сроком";
    private static final boolean READ_ONLY = true;
    private static final boolean REVOKED_TRUE = true;
    private static final boolean REVOKED_FALSE = false;

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Помечает токен как отозванный.
     */
    @Override
    @Transactional
    public void revoke(UUID jti) {
        Optional<RefreshToken> optionalToken = findTokenById(jti);
        optionalToken.ifPresent(this::markAsRevoked);
    }

    /**
     * Проверяет, отозван ли токен.
     */
    @Override
    @Transactional(readOnly = READ_ONLY)
    public boolean isRevoked(UUID jti) {
        Optional<RefreshToken> optionalToken = findTokenById(jti);
        return optionalToken.map(RefreshToken::isRevoked).orElse(REVOKED_FALSE);
    }

    /**
     * Удаляет просроченные токены ежедневно по расписанию.
     */
    @Override
    @Transactional
    @Scheduled(cron = DAILY_CLEANUP_CRON, zone = MOSCOW_ZONE)
    public void cleanExpired() {
        long removedCount = deleteExpiredTokens();
        logCleanupResult(removedCount);
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Находит токен по идентификатору.
     */
    private Optional<RefreshToken> findTokenById(UUID jti) {
        return refreshTokenRepository.findById(jti);
    }

    /**
     * Устанавливает токену флаг revoked=true и сохраняет.
     */
    private void markAsRevoked(RefreshToken token) {
        token.setRevoked(REVOKED_TRUE);
        saveToken(token);
    }

    /**
     * Сохраняет токен в базе данных.
     */
    private void saveToken(RefreshToken token) {
        refreshTokenRepository.save(token);
    }

    /**
     * Удаляет просроченные токены из базы.
     */
    private long deleteExpiredTokens() {
        LocalDateTime now = getCurrentTime();
        return refreshTokenRepository.deleteByExpiresAtBefore(now);
    }

    /**
     * Логирует количество удалённых токенов.
     */
    private void logCleanupResult(long count) {
        log.info(CLEANUP_LOG_MESSAGE, count);
    }

    /**
     * Возвращает текущее время в московском часовом поясе.
     */
    private LocalDateTime getCurrentTime() {
        return LocalDateTime.now(ZoneId.of(MOSCOW_ZONE));
    }
}

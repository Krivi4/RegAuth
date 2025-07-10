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
 * Сервис для управления чёрным списком access‑токенов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultAccessTokenBlackListService implements AccessTokenBlacklistService {

    private static final String MOSCOW_ZONE = "Europe/Moscow";
    private static final String DAILY_CLEANUP_CRON = "0 0 0 * * *";
    private static final String CLEANUP_LOG_MESSAGE =
            "Очищены {} аннулированные access‑токены с истекшим сроком";
    private static final boolean READ_ONLY = true;

    private final RevokedAccessTokenRepository revokedTokenRepo;

    /**
     * Добавляет токен в чёрный список с датой истечения.
     */
    @Override
    @Transactional
    public void block(UUID jti, Instant expiresAt) {
        RevokedAccessToken revokedToken = buildRevokedToken(jti, expiresAt);
        saveRevokedToken(revokedToken);
    }

    /**
     * Проверяет, заблокирован ли токен.
     */
    @Override
    @Transactional(readOnly = READ_ONLY)
    public boolean isBlocked(UUID jti) {
        return existsInBlacklist(jti);
    }

    /**
     * Очищает чёрный список от просроченных токенов.
     * Запускается ежедневно по расписанию.
     */
    @Override
    @Transactional
    @Scheduled(cron = DAILY_CLEANUP_CRON, zone = MOSCOW_ZONE)
    public void cleanExpired() {
        long count = deleteExpiredTokens();
        logCleanupResult(count);
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Создаёт сущность заблокированного токена с датой истечения.
     */
    private RevokedAccessToken buildRevokedToken(UUID jti, Instant expiresAt) {
        LocalDateTime expiresDateTime = toMoscowTime(expiresAt);
        return new RevokedAccessToken(jti, expiresDateTime);
    }

    /**
     * Сохраняет заблокированный токен в базе данных.
     */
    private void saveRevokedToken(RevokedAccessToken revokedToken) {
        revokedTokenRepo.save(revokedToken);
    }

    /**
     * Проверяет наличие токена в чёрном списке.
     */
    private boolean existsInBlacklist(UUID jti) {
        return revokedTokenRepo.existsById(jti);
    }

    /**
     * Удаляет все токены, срок действия которых истёк.
     */
    private long deleteExpiredTokens() {
        LocalDateTime now = nowMoscowTime();
        return revokedTokenRepo.deleteByExpiresAtBefore(now);
    }

    /**
     * Логирует результат очистки чёрного списка.
     */
    private void logCleanupResult(long removedCount) {
        log.info(CLEANUP_LOG_MESSAGE, removedCount);
    }

    /**
     * Преобразует Instant в московское время.
     */
    private LocalDateTime toMoscowTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.of(MOSCOW_ZONE));
    }

    /**
     * Возвращает текущее время в московском часовом поясе.
     */
    private LocalDateTime nowMoscowTime() {
        return LocalDateTime.now(ZoneId.of(MOSCOW_ZONE));
    }
}

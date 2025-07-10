package ru.krivi4.regauth.services.otp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.repositories.OtpRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Сервис для плановой очистки устаревших OTP-кодов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultOtpCleanService implements OtpCleanService {

    private static final String DAILY_CLEANUP_CRON = "0 0 0 * * *";
    private static final String MOSCOW_ZONE = "Europe/Moscow";
    private static final String CLEANUP_LOG_MESSAGE = "Очищено {} устаревших OTP токенов";

    private final OtpRepository otpRepository;

    /**
     * Ежедневно в полночь по Москве удаляет устаревшие OTP-коды и логирует результат.
     */
    @Override
    @Transactional
    @Scheduled(cron = DAILY_CLEANUP_CRON, zone = MOSCOW_ZONE)
    public void cleanExpiredOTP() {
        long deletedCount = removeExpiredOtps();
        logCleanupResult(deletedCount);
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Удаляет из базы OTP-коды, срок действия которых истёк.
     */
    private long removeExpiredOtps() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(MOSCOW_ZONE));
        return otpRepository.deleteByExpiresAtOTPBefore(now);
    }

    /**
     * Логирует количество удалённых OTP-кодов.
     */
    private void logCleanupResult(long deletedCount) {
        log.info(CLEANUP_LOG_MESSAGE, deletedCount);
    }
}

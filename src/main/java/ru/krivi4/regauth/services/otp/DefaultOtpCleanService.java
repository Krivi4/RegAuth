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
 * Реализация сервиса для плановой очистки устаревших OTP-кодов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultOtpCleanService implements OtpCleanService {

    private static final String DAILY_CLEANUP_CRON = "0 0 0 * * *";
    private static final String MOSCOW_ZONE = "Europe/Moscow";
    private static final String CLEANUP_LOG_MESSAGE = "Очищенные {} OTP токены с истекшим сроком";

    private final OtpRepository otpRepository;

    /**
     * Каждый день в полночь по московскому времени удаляет устаревшие OTP-коды из базы данных
     * Логирует количество удалённых записей
     */
    @Override
    @Transactional
    @Scheduled(cron = DAILY_CLEANUP_CRON, zone = MOSCOW_ZONE)
    public void cleanExpiredOTP() {
        long removed = otpRepository.deleteByExpiresAtOTPBefore(LocalDateTime.now(ZoneId.of(MOSCOW_ZONE)));
        log.info(CLEANUP_LOG_MESSAGE, removed);
    }
}

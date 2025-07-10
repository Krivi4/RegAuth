package ru.krivi4.regauth.services.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.config.SmsProperties;
import ru.krivi4.regauth.models.Otp;
import ru.krivi4.regauth.ports.otp.OtpGenerator;
import ru.krivi4.regauth.repositories.OtpRepository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сервис для проверки OTP-кодов и управления счётчиком попыток.
 */
@Service
@RequiredArgsConstructor
public class DefaultOtpVerifyService implements OtpVerifyService {

    private static final int ATTEMPT_INCREMENT = 1;

    private final OtpRepository otpRepository;
    private final OtpGenerator otpGenerator;
    private final SmsProperties smsProperties;

    /**
     * Проверяет OTP по ID и коду. Управляет счётчиком попыток и удаляет записи при истечении срока или успехе.
     */
    @Override
    @Transactional
    public boolean verify(UUID otpId, String code) {
        Otp otp = findOtpById(otpId);

        if (otp == null || shouldDeleteOtp(otp)) {
            return false;
        }
        return checkCodeAndHandleResult(otp, code);
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Ищет OTP-код по идентификатору. Если не найден, возвращает null.
     */
    private Otp findOtpById(UUID otpId) {
        return otpRepository.findById(otpId).orElse(null);
    }

    /**
     * Проверяет, истёк ли срок действия или превышен лимит попыток.
     * Если да, удаляет запись и возвращает true.
     */
    private boolean shouldDeleteOtp(Otp otp) {
        if (isExpired(otp) || isAttemptLimitExceeded(otp)) {
            otpRepository.delete(otp);
            return true;
        }
        return false;
    }

    /**
     * Проверяет, истёк ли срок действия OTP.
     */
    private boolean isExpired(Otp otp) {
        return LocalDateTime.now().isAfter(otp.getExpiresAtOTP());
    }

    /**
     * Проверяет, превышен ли лимит попыток.
     */
    private boolean isAttemptLimitExceeded(Otp otp) {
        return otp.getAttempts() >= smsProperties.getAttempts();
    }

    /**
     * Проверяет код, удаляет запись при успехе или увеличивает счётчик попыток при ошибке.
     */
    private boolean checkCodeAndHandleResult(Otp otp, String rawCode) {
        if (otpGenerator.matches(rawCode, otp.getCodeHash())) {
            otpRepository.delete(otp);
            return true;
        }
        incrementAttempts(otp);
        return false;
    }

    /**
     * Увеличивает количество попыток и сохраняет запись.
     */
    private void incrementAttempts(Otp otp) {
        otp.setAttempts(otp.getAttempts() + ATTEMPT_INCREMENT);
        otpRepository.save(otp);
    }
}

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
 * Реализация сервиса для проверки одноразовых кодов OTP и управления счётчиком попыток.
 */
@Service
@RequiredArgsConstructor
public class DefaultOtpVerifyService implements OtpVerifyService {

    private static final int ATTEMPT_INCREMENT = 1;

    private final OtpRepository otpRepository;
    private final OtpGenerator otpGenerator;
    private final SmsProperties smsProperties;

    /**
     * Проверяет код OTP по его идентификатору
     * Если код истёк или превышен лимит попыток, возвращает false и удаляет запись
     * Если код верный, удаляет запись и возвращает true
     * Если код неверный, увеличивает счётчик попыток и возвращает false
     */
    @Override
    @Transactional
    public boolean verify(UUID otpId, String code) {
        Otp otp = findOtpOrNull(otpId);

        if (otp == null || isExpiredOrOverLimit(otp)) {
            return false;
        }
        return checkCodeAndUpdateAttempts(otp, code);
    }

    /**
     * Ищет запись OTP по идентификатору
     * Если не найдена, возвращает null
     */
    private Otp findOtpOrNull(UUID otpId) {
        return otpRepository.findById(otpId).orElse(null);
    }

    /**
     * Проверяет, истёк ли срок действия OTP или превышено количество попыток
     * Если условие выполнено, удаляет запись из базы и возвращает true
     * Если нет, возвращает false
     */
    private boolean isExpiredOrOverLimit(Otp otp) {
        boolean expired = LocalDateTime.now().isAfter(otp.getExpiresAtOTP());
        boolean overLimit = otp.getAttempts() >= smsProperties.getAttempts();

        if (expired || overLimit) {
            otpRepository.delete(otp);
            return true;
        }
        return false;
    }

    /**
     * Проверяет правильность введённого кода и обновляет счётчик попыток
     * Если код верный, удаляет запись и возвращает true
     * Если код неверный, увеличивает счётчик попыток и возвращает false
     */
    private boolean checkCodeAndUpdateAttempts(Otp otp, String rawCode) {
        boolean matches = otpGenerator.matches(rawCode, otp.getCodeHash());

        if (matches) {
            otpRepository.delete(otp);
            return true;
        }
        incrementAttempts(otp);
        return false;
    }

    /**
     * Увеличивает количество попыток ввода кода на 1 и сохраняет обновлённую запись в базе
     */
    private void incrementAttempts(Otp otp) {
        otp.setAttempts(otp.getAttempts() + ATTEMPT_INCREMENT);
        otpRepository.save(otp);
    }
}

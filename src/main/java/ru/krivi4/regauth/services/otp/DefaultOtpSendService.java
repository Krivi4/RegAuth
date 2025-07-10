package ru.krivi4.regauth.services.otp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.config.SmsProperties;
import ru.krivi4.regauth.models.Otp;
import ru.krivi4.regauth.ports.otp.OtpGenerator;
import ru.krivi4.regauth.ports.otp.OtpSender;
import ru.krivi4.regauth.repositories.OtpRepository;
import ru.krivi4.regauth.services.message.MessageService;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сервис генерации и отправки одноразового кода OTP.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultOtpSendService implements OtpSendService {

    private static final String SERVICE_NAME = "RegAuth";
    private static final String OTP_MESSAGE_KEY = "otp.sms.message";
    private static final int INITIAL_ATTEMPTS = 0;
    private static final String DEBUG_LOG_MESSAGE = "DEBUG OTP: {}";

    private final OtpGenerator otpGenerator;
    private final OtpSender otpSender;
    private final OtpRepository otpRepository;
    private final SmsProperties smsProperties;
    private final MessageService messageService;

    /**
     * Удаляет старые коды для номера, создаёт новый код,
     * сохраняет его в БД и отправляет SMS.
     */
    @Override
    @Transactional
    public UUID send(String phoneNumber) {
        deletePreviousOtps(phoneNumber);

        String rawCode = generateRawOtpCode();
        sendSmsWithOtp(phoneNumber, rawCode);

        Otp otpEntity = createOtpEntity(phoneNumber, rawCode);
        persistOtpEntity(otpEntity);

        logOtpForDebug(rawCode);
        return otpEntity.getIdOtp();
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Удаляет старые OTP-коды для указанного номера.
     */
    private void deletePreviousOtps(String phoneNumber) {
        otpRepository.deleteByPhoneNumber(phoneNumber);
    }

    /**
     * Генерирует новый одноразовый код.
     */
    private String generateRawOtpCode() {
        return otpGenerator.generateCode();
    }

    /**
     * Отправляет SMS с кодом пользователю.
     */
    private void sendSmsWithOtp(String phoneNumber, String rawCode) {
        String message = String.format(
                messageService.getMessage(OTP_MESSAGE_KEY),
                SERVICE_NAME, rawCode
        );
        otpSender.sendRequest(phoneNumber, message);
    }

    /**
     * Создаёт сущность OTP для сохранения в базе.
     */
    private Otp createOtpEntity(String phoneNumber, String rawCode) {
        return new Otp()
                .setIdOtp(UUID.randomUUID())
                .setPhoneNumber(phoneNumber)
                .setCodeHash(otpGenerator.hash(rawCode))
                .setExpiresAtOTP(LocalDateTime.now().plusMinutes(smsProperties.getTtlMinutes()))
                .setAttempts(INITIAL_ATTEMPTS);
    }

    /**
     * Сохраняет OTP-код в базе данных.
     */
    private void persistOtpEntity(Otp otp) {
        otpRepository.save(otp);
    }

    /**
     * Логирует OTP-код для отладки.
     */
    private void logOtpForDebug(String rawCode) {
        log.debug(DEBUG_LOG_MESSAGE, rawCode);
    }
}

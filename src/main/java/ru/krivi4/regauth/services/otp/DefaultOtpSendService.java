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
 * Реализация сервиса генерации и отправки одноразового кода OTP.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultOtpSendService implements OtpSendService {

    private static final String SERVICE_NAME = "RegAuth";
    private static final String OTP_MESSAGE_KEY = "otp.sms.message";
    private static final int INITIAL_ATTEMPTS = 0;
    private static final String DEBUG_LOG_MESSAGE = "DEBUG Otp: {}";

    private final OtpGenerator otpGenerator;
    private final OtpSender otpSender;
    private final OtpRepository otpRepository;
    private final SmsProperties smsProperties;
    private final MessageService messageService;

    /**
     * Удаляет старые коды OTP для номера, генерирует новый код, сохраняет его в БД и отправляет SMS пользователю
     * Возвращает UUID созданного кода
     */
    @Override
    @Transactional
    public UUID send(String phoneNumber) {
        otpRepository.deleteByPhoneNumber(phoneNumber);

        String rawCode = otpGenerator.generateCode();
        String message = String.format(messageService.getMessage(OTP_MESSAGE_KEY), SERVICE_NAME, rawCode);
        otpSender.sendRequest(phoneNumber, message);

        Otp otp = createOtpEntity(phoneNumber, rawCode);
        otpRepository.save(otp);

        log.debug(DEBUG_LOG_MESSAGE, rawCode);
        return otp.getIdOtp();
    }

    /**
     * Создаёт сущность Otp с новым UUID, зашифрованным кодом и сроком действия
     * Устанавливает счётчик попыток в ноль
     */
    private Otp createOtpEntity(String phoneNumber, String rawCode) {
        return new Otp()
                .setIdOtp(UUID.randomUUID())
                .setPhoneNumber(phoneNumber)
                .setCodeHash(otpGenerator.hash(rawCode))
                .setExpiresAtOTP(LocalDateTime.now().plusMinutes(smsProperties.getTtlMinutes()))
                .setAttempts(INITIAL_ATTEMPTS);
    }
}

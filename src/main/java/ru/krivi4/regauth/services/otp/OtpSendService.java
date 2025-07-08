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
 * Сервис генерации и отправки одноразового кода (OTP) на телефон.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OtpSendService {

    private final OtpGenerator otpGenerator;
    private final OtpSender otpSender;
    private final OtpRepository otpRepository;
    private final SmsProperties smsProperties;
    private final MessageService messageService;

    private static final String SERVICE_NAME = "RegAuth";
    private static final int INITIAL_ATTEMPTS = 0;
    /**
     * Генерирует новый одноразовый код, удаляет старые записи для номера,
     * шифрует и сохраняет новую запись в БД, отправляет SMS.
     */
    @Transactional
    public UUID send(String phoneNumber) {
        otpRepository.deleteByPhoneNumber(phoneNumber);

        String rawCode = otpGenerator.generateCode();
        String message = String.format(messageService.getMessage("otp.sms.message"), SERVICE_NAME, rawCode);
        otpSender.sendRequest(phoneNumber, message);

        Otp otp = createOtpEntity(phoneNumber, rawCode);
        otpRepository.save(otp);

        log.debug("DEBUG Otp: {}", rawCode); //TODO Раскомментировать для тестов(вывод кода в логах)
        return otp.getIdOtp();
    }

    // *--------------Вспомагалтельыне метода------------*//

    /**
     * Создаёт сущность Otp с новым ID, хешем кода, временем истечения и начальным счётчиком попыток.
     */
    private Otp createOtpEntity(String phoneNumber, String rawCode) {
        return new Otp()
                .setIdOtp(UUID.randomUUID())
                .setPhoneNumber(phoneNumber)
                .setCodeHash(otpGenerator.hash(rawCode))
                .setExpiresAtOTP(
                LocalDateTime.now().plusMinutes(smsProperties.getTtlMinutes())
        )
                .setAttempts(INITIAL_ATTEMPTS);
    }
}

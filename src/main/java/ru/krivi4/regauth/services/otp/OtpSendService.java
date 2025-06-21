package ru.krivi4.regauth.services.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.config.SmsProperties;
import ru.krivi4.regauth.models.Otp;
import ru.krivi4.regauth.ports.otp.OtpGenerator;
import ru.krivi4.regauth.ports.otp.OtpSender;
import ru.krivi4.regauth.repositories.OtpRepository;

import java.time.LocalDateTime;
import java.util.UUID;

/**Сервис генерации и отправки одноразового кода на телефон.*/
@Service
@RequiredArgsConstructor
public class OtpSendService {

  private final OtpGenerator otpGenerator;
  private final OtpSender otpSender;
  private final OtpRepository otpRepository;
  private final SmsProperties smsProperties;

  /**Генерирует, шифрует, сохраняет и отправляет Otp; возвращает UUID записи.*/
  @Transactional
  public UUID send(String phoneNumber) {
    otpRepository.deleteByPhoneNumber(phoneNumber);

    String code = otpGenerator.generateCode();
    otpSender.sendRequest(phoneNumber,
      "Код подтверждения для сервиса RegAuth: " + code);

    Otp otp = new Otp();
    otp.setIdOtp(UUID.randomUUID());
    otp.setPhoneNumber(phoneNumber);
    otp.setCodeHash(otpGenerator.hash(code));
    otp.setExpiresAtOTP(
      LocalDateTime.now().plusMinutes(smsProperties.getTtlMinutes())
    );
    otp.setAttempts(0);
    System.out.println("DEBUG Otp: " + code); //TODO Раскомментировать для тестов(вывод кода в логах)
    otpRepository.save(otp);

    return otp.getIdOtp();
  }
}

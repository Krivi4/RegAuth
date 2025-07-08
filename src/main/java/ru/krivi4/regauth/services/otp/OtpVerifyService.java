package ru.krivi4.regauth.services.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.models.Otp;
import ru.krivi4.regauth.ports.otp.OtpGenerator;
import ru.krivi4.regauth.config.SmsProperties;
import ru.krivi4.regauth.repositories.OtpRepository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сервис для проверки одноразовых кодов (OTP) и управления счётчиком попыток.
 */
@Service
@RequiredArgsConstructor
public class OtpVerifyService {

  private final OtpRepository otpRepository;
  private final OtpGenerator otpGenerator;
  private final SmsProperties smsProperties;

  private static final int ATTEMPT_INCREMENT = 1;

  /**
   * Проверяет код на корректность и актуальность.
   */
  @Transactional
  public boolean verify(UUID otpId, String code) {
    Otp otp = findOtpOrNull(otpId);

    if (otp == null || isExpiredOrOverLimit(otp)) {
      return false;
    }
    return checkCodeAndUpdateAttempts(otp, code);
  }

  //*-------------------Вспомогательные методы---------------*//

  /**
   * Находит запись Otp по идентификатору или возвращает null.
   */
  private Otp findOtpOrNull(UUID otpId) {
    return otpRepository.findById(otpId).orElse(null);
  }

  /**
   * Проверяет, истёк ли срок действия кода или превышено количество попыток.
   * Если условие выполнено, удаляет запись из базы и возвращает true.
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
   * Проверяет введённый код и обновляет счётчик попыток.
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
   * Увеличивает счётчик попыток на ATTEMPT_INCREMENT и сохраняет запись в базе.
   */
  private void incrementAttempts(Otp otp) {
    otp.setAttempts(otp.getAttempts() + ATTEMPT_INCREMENT);
    otpRepository.save(otp);
  }
}

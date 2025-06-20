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

/**Проверяет введённый пользователем Otp-код и управляет счётчиком попыток.*/
@Service
@RequiredArgsConstructor
public class OtpVerifyService {

  private final OtpRepository otpRepository;
  private final OtpGenerator otpGenerator;
  private final SmsProperties smsProperties;

  /**
   * Возвращает true, если код верный и не просрочен
   * иначе увелечение попытки и возврат false
   */
  @Transactional
  public boolean verify(UUID otpId, String code) {

    Otp otp = otpRepository.findById(otpId).orElse(null);

    if (otp == null) {
      return false;
    } else {
      boolean expired = LocalDateTime.now().isAfter(otp.getExpiresAtOTP());
      boolean overLimit = otp.getAttempts() >= smsProperties.getAttempts();
      if (expired || overLimit) {
        otpRepository.delete(otp);
        return false;
      }

      boolean ok = otpGenerator.matches(code, otp.getCodeHash());
      if (ok) {
        otpRepository.delete(otp);
        return true;
      } else {
        otp.setAttempts(otp.getAttempts() + 1);
        otpRepository.save(otp);
        return false;
      }
    }
  }
}

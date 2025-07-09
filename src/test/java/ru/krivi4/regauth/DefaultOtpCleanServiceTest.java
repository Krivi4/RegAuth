package ru.krivi4.regauth;


import org.junit.jupiter.api.Test;
import ru.krivi4.regauth.repositories.OtpRepository;
import ru.krivi4.regauth.services.otp.DefaultOtpCleanService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Метод cleanExpiredOTP() обязан
 * один раз вызвать репозиторий с текущим временем.
 */
class DefaultOtpCleanServiceTest {

  /**
   * Проверяет, что cleanExpiredOTP() один раз вызывает
   * deleteByExpiresAtOTPBefore().
   */
  @Test
  void cleanExpiredOTP_shouldInvokeRepository() {
    OtpRepository repo = mock(OtpRepository.class);
    when(repo.deleteByExpiresAtOTPBefore(any()))
      .thenReturn(3);

    DefaultOtpCleanService svc = new DefaultOtpCleanService(repo);
    svc.cleanExpiredOTP();

    verify(repo, times(1))
      .deleteByExpiresAtOTPBefore(any(LocalDateTime.class));
  }
}
package ru.krivi4.regauth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.krivi4.regauth.config.SmsProperties;
import ru.krivi4.regauth.models.Otp;
import ru.krivi4.regauth.ports.otp.OtpGenerator;
import ru.krivi4.regauth.repositories.OtpRepository;
import ru.krivi4.regauth.services.otp.DefaultOtpVerifyService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Сценарии: успешное подтверждение, просрочка
 * и превышение лимита попыток.
 */
class DefaultOtpVerifyServiceTest {

  private OtpRepository repo;
  private OtpGenerator generator;
  private DefaultOtpVerifyService service;

  private static final String RAW_CODE = "123456";
  private static final String HASHED = "hash";

  private Otp otp;

  /** Инициализирует моки, свойства и сервис перед каждым тестом.*/
  @BeforeEach
  void setUp() {
    repo = mock(OtpRepository.class);
    generator = mock(OtpGenerator.class);

    SmsProperties props = new SmsProperties();
    props.setAttempts(3);

    service = new DefaultOtpVerifyService(repo, generator, props);

    otp = new Otp();
    otp.setIdOtp(UUID.randomUUID());
    otp.setCodeHash(HASHED);
    otp.setExpiresAtOTP(LocalDateTime.now().plusMinutes(5));
    otp.setAttempts(0);
  }

  /**
   * Корректный код -> возвращается true,
   * запись удаляется из БД.
   */
  @Test
  void verify_ok() {
    when(repo.findById(otp.getIdOtp())).thenReturn(Optional.of(otp));
    when(generator.matches(RAW_CODE, HASHED)).thenReturn(true);

    boolean result = service.verify(otp.getIdOtp(), RAW_CODE);

    assertThat(result).isTrue();
    verify(repo).delete(otp);
  }

  /**
   * Истёкший код -> false,
   * данные удаляются.
   */
  @Test
  void verify_expired() {
    otp.setExpiresAtOTP(LocalDateTime.now().minusMinutes(1));
    when(repo.findById(otp.getIdOtp())).thenReturn(Optional.of(otp));

    assertThat(service.verify(otp.getIdOtp(), RAW_CODE)).isFalse();
    verify(repo).delete(otp);
  }

  /**
   * Неверный код + превышен лимит -> false,
   * запись удаляется.
   */
  @Test
  void verify_overLimit() {
    otp.setAttempts(3);
    when(repo.findById(otp.getIdOtp())).thenReturn(Optional.of(otp));

    boolean result = service.verify(otp.getIdOtp(), RAW_CODE);

    assertThat(result).isFalse();
    verify(repo).delete(otp);
  }
}

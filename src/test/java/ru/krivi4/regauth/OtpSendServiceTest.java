/*
package ru.krivi4.regauth;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.krivi4.regauth.config.SmsProperties;
import ru.krivi4.regauth.models.Otp;
import ru.krivi4.regauth.ports.otp.OtpGenerator;
import ru.krivi4.regauth.ports.otp.OtpSender;
import ru.krivi4.regauth.repositories.OtpRepository;
import ru.krivi4.regauth.services.otp.OtpSendService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

*/
/**
 * Проверяем полный happy-path: очистка,
 * отправка SMS, сохранение и корректный TTL.
 *//*

class OtpSendServiceTest {

  private OtpRepository repo;
  private OtpGenerator  gen;
  private OtpSender     sender;
  private OtpSendService service;

  private SmsProperties props;


  */
/**Инициализирует моки, свойства TTL и OtpSendService.*//*

  @BeforeEach
  void setUp() {
    repo   = mock(OtpRepository.class);
    gen    = mock(OtpGenerator.class);
    sender = mock(OtpSender.class);

    props = new SmsProperties();
    props.setTtlMinutes(5);

    service = new OtpSendService(gen, sender, repo, props);

    when(gen.generateCode()).thenReturn("654321");
    when(gen.hash("654321")).thenReturn("HASH");
  }

  */
/**
   * Метод должен:
   * Удалить старые Otp для этого телефона;
   * Вызвать sendRequest() с верным текстом;
   * Сохранить запись с указанным TTL и хешем;
   * Вернуть сгенерированный UUID.
   *//*

  @Test
  void send_shouldGenerateAndPersistOtp() {
    String phone = "+79994443322";

    ArgumentCaptor<Otp> captor = ArgumentCaptor.forClass(Otp.class);
    when(repo.save(captor.capture())).thenAnswer(a -> a.getArgument(0));

    UUID returned = service.send(phone);

    verify(repo).deleteByPhoneNumber(phone);

    verify(sender).sendRequest(eq(phone),contains("654321"));

    Otp saved = captor.getValue();
    assertThat(saved.getIdOtp()).isEqualTo(returned);
    assertThat(saved.getCodeHash()).isEqualTo("HASH");
    assertThat(Duration.between(
      LocalDateTime.now(),
      saved.getExpiresAtOTP()
    ).toMinutes()).isBetween(4L, 6L);

    assertThat(saved.getPhoneNumber()).isEqualTo(phone);
  }
}
*/

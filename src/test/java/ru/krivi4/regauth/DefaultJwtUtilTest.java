package ru.krivi4.regauth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import ru.krivi4.regauth.jwt.util.DefaultJwtUtil;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** Проверяем корректность создания и декодирования токенов.*/
class DefaultJwtUtilTest {

  private DefaultJwtUtil defaultJwtUtil;

  /**Инициализирует экземпляр DefaultJwtUtil и задаёт тестовый секрет для генерации токенов.*/
  @BeforeEach
  void init() {
    defaultJwtUtil = new DefaultJwtUtil();
    ReflectionTestUtils.setField(
            defaultJwtUtil, "secret",
     "SuperSecretForUnitTests" + UUID.randomUUID()
    );
  }

  /**
   * После генерации access-токена
   * метод DefaultJwtUtil.decode(String) должен
   * успешно вернуть тот же username и фазу FULL.
   */
  @Test
  void accessToken_roundTrip() {
    String token = defaultJwtUtil.generateAccessToken("tester");
    DecodedJWT jwt = defaultJwtUtil.decode(token);

    assertThat(jwt.getClaim("username").asString()).isEqualTo("tester");
    assertThat(jwt.getClaim("phase").asString()).isEqualTo("FULL");
  }

  /**
   * Otp-токен логина обязан включать id Otp
   * и фазу OTP_PENDING.
   */
  @Test
  void otpLoginToken_containsOtpIdAndPhase() {
    UUID otpId = UUID.randomUUID();
    String token = defaultJwtUtil.generateOtpLoginToken("tester", otpId);

    DecodedJWT jwt = defaultJwtUtil.decode(token);
    assertThat(jwt.getClaim("idOtp").asString()).isEqualTo(String.valueOf(otpId));
    assertThat(jwt.getClaim("phase").asString()).isEqualTo("OTP_PENDING");
  }
}
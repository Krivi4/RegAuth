package ru.krivi4.regauth;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import ru.krivi4.regauth.security.filter.JwtLogoutSuccessHandler;
import ru.krivi4.regauth.services.tokens.JwtBlackListService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Проверяем, что JTI попадает в BlackList, а клиент
 * получает HTTP 200 с сообщением «Logged out».
 */
class JwtLogoutSuccessHandlerTest {

  private JwtBlackListService blacklist;
  private JwtLogoutSuccessHandler handler;

  private String token;
  private UUID jti = UUID.randomUUID();
  private Instant exp = Instant.now().plus(10, ChronoUnit.MINUTES);

  /**
   * Подготавливает мок-сервис, экземпляр обработчика и
   * тестовый JWT с заранее известным JTI и временем истечения.
   */
  @BeforeEach
  void init() {
    blacklist = mock(JwtBlackListService.class);
    handler = new JwtLogoutSuccessHandler(blacklist);

    token = JWT.create()
      .withJWTId(jti.toString())
      .withExpiresAt(java.util.Date.from(exp))
      .sign(Algorithm.HMAC256("dummy"));
  }

  /**
   * Метод block() вызывается ровно один раз
   * с верными аргументами.
   */
  @Test
  void onLogoutSuccess_shouldBlockToken() throws Exception {
    MockHttpServletRequest req  = new MockHttpServletRequest();
    MockHttpServletResponse res = new MockHttpServletResponse();
    req.addHeader("Authorization", "Bearer " + token);

    handler.onLogoutSuccess(req, res, null);

    verify(blacklist, times(1))
      .block(eq(jti), any(Instant.class));

    assertThat(res.getStatus()).isEqualTo(200);
    assertThat(res.getContentAsString()).contains("Logged out");
  }
}
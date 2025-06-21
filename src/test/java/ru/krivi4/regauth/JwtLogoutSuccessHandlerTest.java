package ru.krivi4.regauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import ru.krivi4.regauth.security.filter.JwtLogoutSuccessHandler;
import ru.krivi4.regauth.services.tokens.AccessTokenBlackListService;
import ru.krivi4.regauth.services.tokens.RefreshTokenBlackListService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**Проверка блокировки access- и отзыва refresh-токенов.*/
public class JwtLogoutSuccessHandlerTest {

  private AccessTokenBlackListService  accessBL;
  private RefreshTokenBlackListService refreshBL;
  private JwtLogoutSuccessHandler      handler;

  private String accessJwt;
  private String refreshJwt;

  private final UUID accessJti  = UUID.randomUUID();
  private final UUID refreshJti = UUID.randomUUID();
  private final Instant exp     = Instant.now().plus(10, ChronoUnit.MINUTES);

  /** Мокаем сервисы и готовим два JWT с заданными JTI и сроком жизни. */
  @BeforeEach
  void init() {
    accessBL  = mock(AccessTokenBlackListService.class);
    refreshBL = mock(RefreshTokenBlackListService.class);
    handler   = new JwtLogoutSuccessHandler(accessBL, refreshBL);

    Algorithm algo = Algorithm.HMAC256("dummy");

    accessJwt = JWT.create()
            .withJWTId(accessJti.toString())
            .withExpiresAt(java.util.Date.from(exp))
            .sign(algo);

    refreshJwt = JWT.create()
            .withJWTId(refreshJti.toString())
            .withExpiresAt(java.util.Date.from(exp.plus(7, ChronoUnit.DAYS)))
            .sign(algo);
  }
  /**
   * Проверяет, что при logout:
   * - access-токен блокируется методом block();
   * - refresh-токен отзывается методом revoke();
   * - клиенту возвращается HTTP 200 и JSON с текстом «Logged out».
   */
  @Test
  void onLogoutSuccess_blocksAndRevokesTokens() throws Exception {
    MockHttpServletRequest req  = new MockHttpServletRequest();
    MockHttpServletResponse res = new MockHttpServletResponse();

    req.addHeader("Authorization",   "Bearer " + accessJwt);
    req.addHeader("X-Refresh-Token", "Bearer " + refreshJwt);

    handler.onLogoutSuccess(req, res, null);

    verify(accessBL, times(1))
            .block(eq(accessJti), any(Instant.class));

    verify(refreshBL, times(1))
            .revoke(eq(refreshJti));

    assertThat(res.getStatus()).isEqualTo(200);
    assertThat(res.getContentAsString()).contains("Logged out");
  }
}

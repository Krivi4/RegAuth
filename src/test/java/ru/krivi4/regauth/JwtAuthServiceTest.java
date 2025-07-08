/*
package ru.krivi4.regauth;


import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import ru.krivi4.regauth.jwt.phase.Phase;
import ru.krivi4.regauth.jwt.handler.JwtPhaseHandler;
import ru.krivi4.regauth.jwt.util.JwtUtil;
import ru.krivi4.regauth.security.auth.JwtAuthService;
import ru.krivi4.regauth.web.exceptions.PhaseUnknownException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

*/
/**
 * Делегирование в правильный handler при фазе FULL;
 * Выброс PhaseUnknownException при неизвестной фазе.
 *//*

class JwtAuthServiceTest {

  */
/**
   * При валидном токене и поддерживаемой фазе
   * результатом должен быть объект Authentication,
   * возвращённый конкретным обработчиком.
   *//*

  @Test
  void authenticate_shouldDelegateToHandler() {
    JwtUtil util = mock(JwtUtil.class);
    DecodedJWT jwt = mock(DecodedJWT.class);
    Claim fullClaim = mock(Claim.class);

    when(fullClaim.asString()).thenReturn("FULL");
    when(jwt.getClaim("phase")).thenReturn(fullClaim);
    when(util.decode("RAW")).thenReturn(jwt);

    JwtPhaseHandler handler = mock(JwtPhaseHandler.class);
    Authentication expected = mock(Authentication.class);

    when(handler.phase()).thenReturn(Phase.FULL);
    when(handler.handle(jwt)).thenReturn(expected);

    JwtAuthService service = new JwtAuthService(util, Map.of(Phase.FULL, handler));

    assertThat(service.authenticate("RAW")).isSameAs(expected);
  }

  */
/**
   * Если в карте нет обработчика нужной фазы —
   * должно быть выброшено PhaseUnknownException.
   *//*

  @Test
  void authenticate_shouldThrowWhenNoHandler() {
    JwtUtil util = mock(JwtUtil.class);
    DecodedJWT jwt = mock(DecodedJWT.class);
    Claim fakePhase = mock(Claim.class);

    when(fakePhase.asString()).thenReturn("FANTASY");
    when(jwt.getClaim("phase")).thenReturn(fakePhase);
    when(util.decode("RAW")).thenReturn(jwt);

    JwtAuthService service = new JwtAuthService(util, Map.of());

    assertThatThrownBy(() -> service.authenticate("RAW"))
      .isInstanceOf(PhaseUnknownException.class);
  }
}*/

package ru.krivi4.regauth;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.krivi4.regauth.models.RevokedAccessToken;
import ru.krivi4.regauth.repositories.RevokedAccessTokenRepository;
import ru.krivi4.regauth.services.tokens.AccessTokenBlackListService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

/**
 * Проверяем методы block() и
 * cleanExpiredTokens().
 */
class AccessTokenBlackListServiceTest {

  private RevokedAccessTokenRepository repo;
  private AccessTokenBlackListService service;

  /**Инициализирует мок-репозиторий и сам сервис перед каждым тестом.*/
  @BeforeEach
  void init() {
    repo = mock(RevokedAccessTokenRepository.class);
    service = new AccessTokenBlackListService(repo);
  }

  /**
   * После вызова block()
   * в репозиторий должен попасть объект
   * с тем же JTI и датой истечения.
   */
  @Test
  void block_shouldPersistRevokedToken() {
    UUID jti = UUID.randomUUID();
    Instant exp = Instant.now().plusSeconds(600);

    service.block(jti, exp);


    ArgumentCaptor<RevokedAccessToken> captor =
      ArgumentCaptor.forClass(RevokedAccessToken.class);
    verify(repo).save(captor.capture());
    RevokedAccessToken saved = captor.getValue();

    assertThat(saved.getJti()).isEqualTo(jti);
    assertThat(saved.getExpiresAt()).isEqualTo(
        LocalDateTime.ofInstant(exp,
        ZoneId.of("Europe/Moscow"))
      );
  }

  /**
   * Метод очистки обязан один раз
   * вызвать deleteByExpiresAtBefore.
   */
  @Test
  void cleanExpiredTokens_shouldInvokeRepository() {
    when(repo.deleteByExpiresAtBefore(any()))
      .thenReturn(2);

    service.cleanExpired();

    verify(repo, times(1))
      .deleteByExpiresAtBefore(any(LocalDateTime.class));
  }
}
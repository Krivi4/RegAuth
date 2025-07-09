package ru.krivi4.regauth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.krivi4.regauth.models.RefreshToken;
import ru.krivi4.regauth.repositories.RefreshTokenRepository;
import ru.krivi4.regauth.services.tokens.DefaultRefreshTokenBlackListService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Проверяется базовая логика сервиса:
 * метод revoke() помечает токен как отозванный и сохраняет изменения;
 * метод isRevoked() корректно возвращает статус отзыва;
 * метод cleanExpired() вызывает очистку репозитория ровно один раз.
 */
public class DefaultRefreshTokenBlackListServiceTest {

  private RefreshTokenRepository repo;
  private DefaultRefreshTokenBlackListService service;

  private final UUID jti = UUID.randomUUID();

  /**Создаёт мок-репозиторий и сам сервис перед выполнением каждого теста.*/
  @BeforeEach
  void init() {
    repo = mock(RefreshTokenRepository.class);
    service = new DefaultRefreshTokenBlackListService(repo);
  }

  /** revoke() должен установить revoked=true и сохранить сущность. */
  @Test
  void revoke_shouldMarkTokenRevoked() {
    RefreshToken token = new RefreshToken(jti, "user", LocalDateTime.now().plusDays(1), false);
    when(repo.findById(jti)).thenReturn(Optional.of(token));

    service.revoke(jti);

    ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
    verify(repo).save(captor.capture());
    assertThat(captor.getValue().isRevoked()).isTrue();
  }

  /** isRevoked() возвращает true, если флаг установлен. */
  @Test
  void isRevoked_shouldReturnTrueWhenFlagSet() {
    RefreshToken token = new RefreshToken(jti, "user", LocalDateTime.now(), true);
    when(repo.findById(jti)).thenReturn(Optional.of(token));

    assertThat(service.isRevoked(jti)).isTrue();
  }

  /** cleanExpired() вызывает deleteByExpiresAtBefore ровно один раз. */
  @Test
  void cleanExpired_shouldInvokeRepo() {
    when(repo.deleteByExpiresAtBefore(any())).thenReturn(3);
    service.cleanExpired();
    verify(repo, times(1)).deleteByExpiresAtBefore(any());
  }
}
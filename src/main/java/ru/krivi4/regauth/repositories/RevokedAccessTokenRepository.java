package ru.krivi4.regauth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.krivi4.regauth.models.RevokedAccessToken;

import java.time.LocalDateTime;
import java.util.UUID;

/**Репозиторий «чёрного списка» access-токенов.*/
@Repository
public interface RevokedAccessTokenRepository extends JpaRepository<RevokedAccessToken, UUID> {

  /** Очистка просроченных записей */
  int deleteByExpiresAtBefore(LocalDateTime expiresAt);
}

package ru.krivi4.regauth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.krivi4.regauth.models.RefreshToken;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Очистка просроченных токенов
     */
    int deleteByExpiresAtBefore(LocalDateTime expiresAt);

    /**
     * Находит неотозванный токен по JTI.
     */
    Optional<RefreshToken> findByJtiAndRevokedFalse(UUID jti);
}

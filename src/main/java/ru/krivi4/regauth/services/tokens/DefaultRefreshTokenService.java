package ru.krivi4.regauth.services.tokens;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.jwt.util.JwtUtil;
import ru.krivi4.regauth.models.RefreshToken;
import ru.krivi4.regauth.repositories.RefreshTokenRepository;
import ru.krivi4.regauth.services.message.MessageService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для управления refresh‑токенами:
 * создание, валидация, отзыв и очистка.
 */
@Service
@RequiredArgsConstructor
public class DefaultRefreshTokenService implements RefreshTokenService {

    private static final String MOSCOW_ZONE = "Europe/Moscow";
    private static final String USERNAME_CLAIM = "username";
    private static final String HOURLY_CLEANUP_CRON = "0 0 * * * *";
    private static final String INVALID_TOKEN_MSG_KEY = "refresh.token.invalid.exception";
    private static final boolean READ_ONLY = true;
    private static final boolean REVOKED_TRUE = true;
    private static final boolean REVOKED_FALSE = false;

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository tokenRepository;
    private final MessageService messageService;

    /**
     * Сохраняет новый refresh‑токен в базе.
     */
    @Override
    @Transactional
    public void save(String rawToken) {
        RefreshToken token = buildTokenEntity(rawToken);
        saveToken(token);
    }

    /**
     * Отзывает refresh‑токен, помечая его как revoked.
     */
    @Override
    @Transactional
    public void revoked(RefreshToken token) {
        markTokenAsRevoked(token);
        saveToken(token);
    }

    /**
     * Проверяет refresh‑токен на валидность и не истекший срок.
     */
    @Override
    @Transactional(readOnly = READ_ONLY)
    public RefreshToken validate(String rawJwt) {
        DecodedJWT jwt = decodeJwt(rawJwt);
        UUID jti = extractJti(jwt);

        RefreshToken token = findActiveTokenOrThrow(jti);
        rejectIfExpired(token);

        return token;
    }

    /**
     * Ежечасно удаляет просроченные refresh‑токены.
     */
    @Override
    @Transactional
    @Scheduled(cron = HOURLY_CLEANUP_CRON, zone = MOSCOW_ZONE)
    public void purgeExpired() {
        deleteExpiredTokens();
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Декодирует JWT.
     */
    private DecodedJWT decodeJwt(String rawJwt) {
        return jwtUtil.decode(rawJwt);
    }

    /**
     * Извлекает JTI из decoded JWT.
     */
    private UUID extractJti(DecodedJWT jwt) {
        return UUID.fromString(jwt.getId());
    }

    /**
     * Строит сущность RefreshToken из raw JWT.
     */
    private RefreshToken buildTokenEntity(String rawToken) {
        DecodedJWT jwt = decodeJwt(rawToken);
        UUID jti = extractJti(jwt);
        String username = jwt.getClaim(USERNAME_CLAIM).asString();
        LocalDateTime expiresAt = toLocalDateTime(jwt.getExpiresAt().toInstant());

        return new RefreshToken(jti, username, expiresAt, REVOKED_FALSE);
    }

    /**
     * Сохраняет токен в базе.
     */
    private void saveToken(RefreshToken token) {
        tokenRepository.save(token);
    }

    /**
     * Находит активный токен или бросает исключение.
     */
    private RefreshToken findActiveTokenOrThrow(UUID jti) {
        Optional<RefreshToken> tokenOpt = tokenRepository.findByJtiAndRevokedFalse(jti);
        return tokenOpt.orElseThrow(() -> new IllegalArgumentException(
                messageService.getMessage(INVALID_TOKEN_MSG_KEY)));
    }

    /**
     * Проверяет срок действия токена.
     */
    private void rejectIfExpired(RefreshToken token) {
        if (token.getExpiresAt().isBefore(currentTime())) {
            throw new IllegalArgumentException(messageService.getMessage(INVALID_TOKEN_MSG_KEY));
        }
    }

    /**
     * Помечает токен как отозванный.
     */
    private void markTokenAsRevoked(RefreshToken token) {
        token.setRevoked(REVOKED_TRUE);
    }

    /**
     * Удаляет все просроченные токены из базы.
     */
    private void deleteExpiredTokens() {
        tokenRepository.deleteByExpiresAtBefore(currentTime());
    }

    /**
     * Текущее время в московском часовом поясе.
     */
    private LocalDateTime currentTime() {
        return LocalDateTime.now(ZoneId.of(MOSCOW_ZONE));
    }

    /**
     * Конвертирует Instant в LocalDateTime по часовому поясу Москвы.
     */
    private LocalDateTime toLocalDateTime(java.time.Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.of(MOSCOW_ZONE));
    }
}

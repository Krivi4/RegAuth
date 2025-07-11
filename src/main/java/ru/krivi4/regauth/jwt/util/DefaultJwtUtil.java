package ru.krivi4.regauth.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.models.Person;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Генерация и декодировка JWT.
 */
@Component
public class DefaultJwtUtil implements JwtUtil {

    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_PASSWORD = "password";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_PHONE_NUMBER = "phone_number";
    private static final String CLAIM_PHASE = "phase";
    private static final String CLAIM_ID_OTP = "idOtp";
    private static final String PHASE_FULL = "FULL";
    private static final String PHASE_OTP_PENDING = "OTP_PENDING";
    private static final String PHASE_REFRESH = "REFRESH";
    private static final String SUBJECT = "User details";
    private static final String ISSUER = "RegAuth";
    private static final int ACCESS_TOKEN_EXPIRATION_MINUTES = 10;
    private static final int OTP_TOKEN_EXPIRATION_SECONDS = 180;
    private static final int REFRESH_TOKEN_EXPIRATION_DAYS = 7;
    private static final String PROP_JWT_SECRET = "${jwt.secret}";


    @Value(PROP_JWT_SECRET)
    private String secret;

    /**
     * Генерирует access-токен (10 мин).
     */
    @Override
    public String generateAccessToken(String username) {
        return buildToken()
                .withClaim(CLAIM_USERNAME, username)
                .withClaim(CLAIM_PHASE, PHASE_FULL)
                .withExpiresAt(expirationDateMinutes(ACCESS_TOKEN_EXPIRATION_MINUTES))
                .sign(getAlgorithm());
    }

    /**
     * Генерирует Otp-токен для входа (3 мин).
     */
    @Override
    public String generateOtpLoginToken(String username, UUID idOtp) {
        return buildToken()
                .withClaim(CLAIM_USERNAME, username)
                .withClaim(CLAIM_PHASE, PHASE_OTP_PENDING)
                .withClaim(CLAIM_ID_OTP, idOtp.toString())
                .withExpiresAt(expirationDateSeconds(OTP_TOKEN_EXPIRATION_SECONDS))
                .sign(getAlgorithm());
    }

    /**
     * Генерирует Otp-токен для регистрации (3 мин).
     */
    @Override
    public String generateOtpRegistrationToken(Person person, UUID idOtp) {
        return buildToken()
                .withClaim(CLAIM_USERNAME, person.getUsername())
                .withClaim(CLAIM_PASSWORD, person.getPassword())
                .withClaim(CLAIM_EMAIL, person.getEmail())
                .withClaim(CLAIM_PHONE_NUMBER, person.getPhoneNumber())
                .withClaim(CLAIM_PHASE, PHASE_OTP_PENDING)
                .withClaim(CLAIM_ID_OTP, idOtp.toString())
                .withExpiresAt(expirationDateSeconds(OTP_TOKEN_EXPIRATION_SECONDS))
                .sign(getAlgorithm());
    }

    /**
     * Генерирует refresh-токен (7 дней).
     */
    @Override
    public String generateRefreshToken(String username) {
        return buildToken()
                .withClaim(CLAIM_USERNAME, username)
                .withClaim(CLAIM_PHASE, PHASE_REFRESH)
                .withExpiresAt(expirationDateDays(REFRESH_TOKEN_EXPIRATION_DAYS))
                .sign(getAlgorithm());
    }

    /**
     * Декодирует JWT без извлечения имени.
     */
    @Override
    public DecodedJWT decode(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(getAlgorithm())
                .withSubject(SUBJECT)
                .withIssuer(ISSUER)
                .build();
        return verifier.verify(token);
    }

    //*----------Вспомогательные методы---------*//

    /**
     * Создаёт базовый JWT-билдер с общими данными.
     */
    private Builder buildToken() {
        return JWT.create()
                .withSubject(SUBJECT)
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(new Date())
                .withIssuer(ISSUER);
    }

    /**
     * Возвращает алгоритм подписи.
     */
    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    /**
     * Возвращает дату истечения токена через N минут.
     */
    private Date expirationDateMinutes(int minutes) {
        return Date.from(ZonedDateTime.now().plusMinutes(minutes).toInstant());
    }

    /**
     * Возвращает дату истечения токена через N секунд.
     */
    private Date expirationDateSeconds(int seconds) {
        return Date.from(Instant.now().plusSeconds(seconds));
    }

    /**
     * Возвращает дату истечения токена через N дней.
     */
    private Date expirationDateDays(int days) {
        return Date.from(ZonedDateTime.now().plusDays(days).toInstant());
    }
}

package ru.krivi4.regauth.jwt.util;

import com.auth0.jwt.JWT;
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

/**Генерации и декодировка JWT*/
@Component
public class JwtUtil {

    @Value("${jwt_secret}")
    private String secret;

    /**Генерирует access-токен (10 мин).*/
  public String generateAccessToken(String username) {
    Date expirationDateAT = Date.from(ZonedDateTime.now().plusMinutes(10).toInstant());
    return JWT.create()
      .withSubject("User details")
      .withClaim("username", username)
      .withClaim("phase", "FULL")
      .withJWTId(String.valueOf(UUID.randomUUID()))
      .withIssuedAt(new Date())
      .withIssuer("RegAuth")
      .withExpiresAt(expirationDateAT)
      .sign(Algorithm.HMAC256(secret));
  }

 /** Генерирует Otp-токен для входа (3 мин).*/
  public String generateOtpLoginToken(String username, UUID idOtp) {
    Date expirationDateOtp = Date.from(Instant.now().plusSeconds(180)); // 3 мин
    return JWT.create()
      .withSubject("User details")
      .withClaim("username", username)
      .withClaim("phase", "OTP_PENDING")
      .withClaim("idOtp",String.valueOf(idOtp))
      .withJWTId(String.valueOf(UUID.randomUUID()))
      .withIssuedAt(new Date())
      .withIssuer("RegAuth")
      .withExpiresAt(expirationDateOtp)
      .sign(Algorithm.HMAC256(secret));
  }

    /** Генерирует Otp-токен для регистрации (3 мин).*/
  public String generateOtpRegistrationToken(Person person, UUID idOtp) {
    Date expirationDateOtp = Date.from(Instant.now().plusSeconds(180)); // 3 мин
    return JWT.create()
      .withSubject("User details")
      .withClaim("username", person.getUsername())
      .withClaim("password", person.getPassword())
      .withClaim("email", person.getEmail())
      .withClaim("phone_number", person.getPhoneNumber())
      .withClaim("phase", "OTP_PENDING")
      .withClaim("idOtp", String.valueOf(idOtp))
      .withJWTId(String.valueOf(UUID.randomUUID()))
      .withIssuedAt(new Date())
      .withIssuer("RegAuth")
      .withExpiresAt(expirationDateOtp)
      .sign(Algorithm.HMAC256(secret));
  }

  /** Генерирует refresh-токен (7 дней).*/
public String generateRefreshToken(String username) {
  Date expirationDateRT = Date.from(ZonedDateTime.now().plusDays(7).toInstant());
  return JWT.create()
    .withSubject("User details")
    .withClaim("username", username)
    .withClaim("phase", "REFRESH")
    .withJWTId(String.valueOf(UUID.randomUUID()))
    .withIssuedAt(new Date())
    .withIssuer("RegAuth")
    .withExpiresAt(expirationDateRT)
    .sign(Algorithm.HMAC256(secret));
}

    /**Декодирует JWT без извлечения имени.*/
  public DecodedJWT decode(String token) throws JWTVerificationException {
    JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
      .withSubject("User details")
      .withIssuer("RegAuth")
      .build();

    return verifier.verify(token);

  }
}

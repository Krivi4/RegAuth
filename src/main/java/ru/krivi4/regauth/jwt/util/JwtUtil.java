package ru.krivi4.regauth.jwt.util;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import ru.krivi4.regauth.models.Person;

import java.util.UUID;

/**
 * Контракт для генерации и декодирования JWT.
 */
public interface JwtUtil {

    String generateAccessToken(String username);

    String generateOtpLoginToken(String username, UUID idOtp);

    String generateOtpRegistrationToken(Person person, UUID idOtp);

    String generateRefreshToken(String username);

    DecodedJWT decode(String token) throws JWTVerificationException;
}

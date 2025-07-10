package ru.krivi4.regauth.jwt.util;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import ru.krivi4.regauth.models.Person;

import java.util.UUID;

/**
 * Контракт для утилиты работы с JWT токенами.
 * Определяет методы генерации различных типов токенов и их декодирования.
 */
public interface JwtUtil {

    /**
     * Генерирует access токен для пользователя с указанным именем.
     */
    String generateAccessToken(String username);

    /**
     * Генерирует токен для подтверждения входа по одноразовому паролю.
     */
    String generateOtpLoginToken(String username, UUID idOtp);

    /**
     * Генерирует токен для подтверждения регистрации по одноразовому паролю.
     */
    String generateOtpRegistrationToken(Person person, UUID idOtp);

    /**
     * Генерирует refresh токен для обновления access токена без повторной аутентификации.
     */
    String generateRefreshToken(String username);

    /**
     * Декодирует JWT токен и возвращает его содержимое.
     */
    DecodedJWT decode(String token) throws JWTVerificationException;
}

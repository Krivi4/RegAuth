package ru.krivi4.regauth.services.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import ru.krivi4.regauth.models.Person;

/**
 * Контракт сервиса регистрации.
 * Создаёт нового пользователя в базе данных.
 */
public interface RegistrationService {

    /**
     * Регистрирует нового пользователя на основе данных из JWT.
     */
    Person register(DecodedJWT decodedJwt);
}

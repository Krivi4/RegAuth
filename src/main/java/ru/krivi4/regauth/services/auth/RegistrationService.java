package ru.krivi4.regauth.services.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import ru.krivi4.regauth.models.Person;

/**
 * Контракт сервиса регистрации:
 * создание нового пользователя в БД.
 */
public interface RegistrationService {

    Person register(DecodedJWT decodedJwt);
}

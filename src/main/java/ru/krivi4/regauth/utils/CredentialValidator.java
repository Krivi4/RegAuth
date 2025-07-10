package ru.krivi4.regauth.utils;

/**
 * Интерфейс для валидации учетных данных пользователя.
 */
public interface CredentialValidator {

    /**
     * Проверяет пароль на соответствие требованиям.
     */
    void isValidPassword(String password);
}

package ru.krivi4.regauth.utils;

/**
 * Интерфейс валидатора учётных данных.
 */
public interface CredentialValidator {

    void isValidPassword(String password);
}

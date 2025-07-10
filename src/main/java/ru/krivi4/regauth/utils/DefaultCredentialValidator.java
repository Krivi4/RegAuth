package ru.krivi4.regauth.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.services.message.MessageService;

/**
 * Реализация валидатора паролей.
 * Проверяет пароль на соответствие минимальной длине.
 */
@Component
@RequiredArgsConstructor
public class DefaultCredentialValidator implements CredentialValidator {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String PASSWORD_LENGTH_ERROR_KEY = "password.length.validation.exception";

    private final MessageService messageService;

    /**
     * Проверяет, соответствует ли пароль минимальной длине.
     * Если длина меньше допустимой — выбрасывает исключение.
     */
    @Override
    public void isValidPassword(String password) {
        if (isTooShort(password)) {
            throwPasswordTooShortException();
        }
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Проверяет, короче ли пароль минимальной длины.
     */
    private boolean isTooShort(String password) {
        return password.length() < MIN_PASSWORD_LENGTH;
    }

    /**
     * Выбрасывает IllegalArgumentException с сообщением из ресурсов.
     */
    private void throwPasswordTooShortException() {
        throw new IllegalArgumentException(messageService.getMessage(PASSWORD_LENGTH_ERROR_KEY));
    }
}

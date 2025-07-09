package ru.krivi4.regauth.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.services.message.DefaultMessageService;

/**
 * Валидация учётных данных.
 */
@Component
@RequiredArgsConstructor
public class DefaultCredentialValidator implements CredentialValidator {

    private static final int MINIMAL_PASSWORD_LENGTH = 8;
    private static final String PASSWORD_LENGTH_MSG_KEY = "password.length.validation.exception";

    private final DefaultMessageService defaultMessageService;

    @Override
    public void isValidPassword(String password) {
        if (password.length() < MINIMAL_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    defaultMessageService.getMessage(PASSWORD_LENGTH_MSG_KEY)
            );
        }
    }
}

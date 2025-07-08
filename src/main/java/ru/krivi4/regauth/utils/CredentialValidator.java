package ru.krivi4.regauth.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.services.message.MessageService;

@Component
@RequiredArgsConstructor
public class CredentialValidator {
    private static final int MINIMAL_PASSWORD_LENGTH = 8;

    private MessageService messageService;

    public void isValidPassword(String password) {
        if (password.length() < MINIMAL_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(messageService.getMessage("password.length.validation.exception"));
        }
    }
}
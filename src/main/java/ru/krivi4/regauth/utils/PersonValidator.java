package ru.krivi4.regauth.utils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.services.auth.PersonDetailsService;
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.web.exceptions.DuplicatePersonException;

/**
 * Проверяет уникальность username и валидирует пользователя.
 */
@Component
@RequiredArgsConstructor
public class PersonValidator implements Validator {

    private final PersonDetailsService personDetailsService;
    private final MessageService messageService;

    /**
     * Проверяет, поддерживается ли класс для валидации.
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    /**
     * Выполняет валидацию объекта Person.
     */
    @Override
    public void validate(Object target, Errors errors) {
        String username = extractUsername(target);

        if (shouldSkip(username)) {
            return;
        }

        checkUsernameUniqueness(username);
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Извлекает username из объекта Person.
     */
    private String extractUsername(Object target) {
        return ((Person) target).getUsername();
    }

    /**
     * Проверяет, нужно ли пропустить валидацию (пустой username).
     */
    private boolean shouldSkip(String username) {
        return StringUtils.isEmpty(username);
    }

    /**
     * Проверяет уникальность username в базе.
     */
    private void checkUsernameUniqueness(String username) {
        if (personDetailsService.usernameExists(username)) {
            throwDuplicateUsernameException(username);
        }
    }

    /**
     * Бросает исключение о дублировании username.
     */
    private void throwDuplicateUsernameException(String username) {
        throw new DuplicatePersonException(username, messageService);
    }
}

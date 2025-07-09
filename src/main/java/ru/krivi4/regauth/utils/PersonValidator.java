package ru.krivi4.regauth.utils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.services.auth.PersonDetailsService;
import ru.krivi4.regauth.services.message.DefaultMessageService;
import ru.krivi4.regauth.web.exceptions.DuplicatePersonException;

/**
 * Проверяет уникальность имени пользователя при регистрации.
 */
@Component
@RequiredArgsConstructor
public class PersonValidator implements Validator {

    private final PersonDetailsService personDetailsService;
    private final DefaultMessageService defaultMessageService;

    /**
     * Проверяет поддержку класса Person для валидации.
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    /**
     * Валидирует объект Person.
     */
    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        String username = person.getUsername();

        if (shouldSkipValidation(username)) {
            return;
        }

        ensureUsernameIsUnique(username);
    }

    /**
     * Проверяет, нужно ли пропустить валидацию (username пустой).
     */
    private boolean shouldSkipValidation(String username) {
        return StringUtils.isEmpty(username);
    }

    /**
     * Проверяет уникальность username и выбрасывает исключение, если он уже занят.
     */
    private void ensureUsernameIsUnique(String username) {
        if (personDetailsService.usernameExists(username)) {
            throw new DuplicatePersonException(username, defaultMessageService);
        }
    }
}

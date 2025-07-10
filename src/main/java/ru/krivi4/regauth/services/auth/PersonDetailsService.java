package ru.krivi4.regauth.services.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.repositories.PeopleRepository;
import ru.krivi4.regauth.security.auth.PersonDetails;
import ru.krivi4.regauth.services.message.MessageService;

import java.util.Optional;

/**
 * Сервис для загрузки пользователей.
 * Используется Spring Security для аутентификации.
 */
@Service
@RequiredArgsConstructor
public class PersonDetailsService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MESSAGE_KEY = "security.context.user.not.found.exception";
    private static final boolean TRANSACTION_READ_ONLY = true;

    private final PeopleRepository peopleRepository;
    private final MessageService messageService;

    /**
     * Загружает пользователя по username.
     * Возвращает UserDetails для Spring Security или выбрасывает исключение, если пользователь не найден.
     */
    @Override
    @Transactional(readOnly = TRANSACTION_READ_ONLY)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = findPersonByUsername(username);
        return toUserDetails(person);
    }

    /**
     * Проверяет, существует ли пользователь с указанным именем.
     */
    @Transactional(readOnly = TRANSACTION_READ_ONLY)
    public boolean usernameExists(String username) {
        return peopleRepository.existsByUsername(username);
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Ищет пользователя в базе данных по имени.
     * Бросает UsernameNotFoundException, если пользователь не найден.
     */
    private Person findPersonByUsername(String username) {
        Optional<Person> personOptional = peopleRepository.findByUsername(username);
        return personOptional.orElseThrow(() -> createUserNotFoundException(username));
    }

    /**
     * Преобразует объект Person в PersonDetails для Spring Security.
     */
    private PersonDetails toUserDetails(Person person) {
        return new PersonDetails(person);
    }

    /**
     * Создаёт исключение UsernameNotFoundException с локализованным сообщением.
     */
    private UsernameNotFoundException createUserNotFoundException(String username) {
        String message = messageService.getMessage(USER_NOT_FOUND_MESSAGE_KEY, username);
        return new UsernameNotFoundException(message);
    }
}

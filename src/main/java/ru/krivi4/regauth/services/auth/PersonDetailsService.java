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
import ru.krivi4.regauth.services.message.DefaultMessageService;

import java.util.Optional;

/**
 * Адаптер Spring Security: загружает Person
 * из БД по имени пользователя и оборачивает в PersonDetails.
 */
@Service
@RequiredArgsConstructor
public class PersonDetailsService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MESSAGE_KEY = "security.context.user.not.found.exception";
    private static final boolean TRANSACTION_READ_ONLY = true;

    private final PeopleRepository peopleRepository;
    private final DefaultMessageService defaultMessageService;

    /**
     * Загружает UserDetails по username.
     * Если пользователь не найден в базе — бросает UsernameNotFoundException.
     */
    @Override
    @Transactional(readOnly = TRANSACTION_READ_ONLY)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> personOptional = peopleRepository.findByUsername(username);
        Person person = requirePerson(personOptional, username);
        return new PersonDetails(person);
    }

    /**
     * Проверяет, существует ли пользователь с данным именем.
     */
    @Transactional(readOnly = TRANSACTION_READ_ONLY)
    public boolean usernameExists(String username) {
        return peopleRepository.existsByUsername(username);
    }

    /* -------------------- Вспомогательные методы -------------------- */

    /**
     * Проверяет наличие Person в Optional.
     * Если пусто — бросает UsernameNotFoundException с сообщением.
     */
    private Person requirePerson(Optional<Person> personOptional, String username) {
        if (personOptional.isEmpty()) {
            String msg = defaultMessageService.getMessage(
                    USER_NOT_FOUND_MESSAGE_KEY, username
            );
            throw new UsernameNotFoundException(msg);
        }
        return personOptional.get();
    }
}

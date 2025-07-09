package ru.krivi4.regauth.services.person;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.repositories.PeopleRepository;
import ru.krivi4.regauth.services.message.DefaultMessageService;
import ru.krivi4.regauth.web.exceptions.PersonUsernameNotFoundException;

/**
 * Реализация сервиса поиска пользователей по имени или номеру телефона.
 */
@Service
@RequiredArgsConstructor
public class DefaultPersonFindService implements PersonFindService {

    private static final boolean READ_ONLY = true;

    private final PeopleRepository peopleRepository;
    private final DefaultMessageService defaultMessageService;

    /**
     * Находит пользователя по имени в базе данных
     * Если пользователь не найден, выбрасывает исключение PersonUsernameNotFoundException
     */
    @Override
    @Transactional(readOnly = READ_ONLY)
    public Person findByUsername(String username) {
        return peopleRepository.findByUsername(username)
                .orElseThrow(() -> new PersonUsernameNotFoundException(defaultMessageService));
    }

    /**
     * Находит пользователя по номеру телефона в базе данных
     * Если пользователь не найден, выбрасывает исключение PersonUsernameNotFoundException
     */
    @Override
    @Transactional(readOnly = READ_ONLY)
    public Person findByPhoneNumber(String phoneNumber) {
        return peopleRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new PersonUsernameNotFoundException(defaultMessageService));
    }
}

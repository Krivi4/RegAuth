package ru.krivi4.regauth.services.person;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.repositories.PeopleRepository;
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.web.exceptions.PersonUsernameNotFoundException;

/**
 * Сервис для поиска пользователей по имени или номеру телефона.
 */
@Service
@RequiredArgsConstructor
public class DefaultPersonFindService implements PersonFindService {

    private static final boolean READ_ONLY = true;

    private final PeopleRepository peopleRepository;
    private final MessageService messageService;

    /**
     * Ищет пользователя по имени. Если не найден — выбрасывает исключение.
     */
    @Override
    @Transactional(readOnly = READ_ONLY)
    public Person findByUsername(String username) {
        return findPersonByUsernameOrThrow(username);
    }

    /**
     * Ищет пользователя по номеру телефона. Если не найден — выбрасывает исключение.
     */
    @Override
    @Transactional(readOnly = READ_ONLY)
    public Person findByPhoneNumber(String phoneNumber) {
        return findPersonByPhoneNumberOrThrow(phoneNumber);
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Ищет пользователя в БД по имени. Бросает исключение, если не найден.
     */
    private Person findPersonByUsernameOrThrow(String username) {
        return peopleRepository.findByUsername(username)
                .orElseThrow(() -> buildNotFoundException());
    }

    /**
     * Ищет пользователя в БД по телефону. Бросает исключение, если не найден.
     */
    private Person findPersonByPhoneNumberOrThrow(String phoneNumber) {
        return peopleRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> buildNotFoundException());
    }

    /**
     * Создаёт исключение PersonUsernameNotFoundException с локализованным сообщением.
     */
    private PersonUsernameNotFoundException buildNotFoundException() {
        return new PersonUsernameNotFoundException(messageService);
    }
}

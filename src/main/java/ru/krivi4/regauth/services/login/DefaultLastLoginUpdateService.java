package ru.krivi4.regauth.services.login;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.repositories.PeopleRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Сервис для обновления даты последнего входа пользователя.
 */
@Service
@RequiredArgsConstructor
public class DefaultLastLoginUpdateService implements LastLoginUpdateService {

    private static final ZoneId MOSCOW_ZONE = ZoneId.of("Europe/Moscow");

    private final PeopleRepository peopleRepository;

    /**
     * Обновляет поле lastLogin у пользователя текущей датой и временем.
     * Сохраняет изменения в базе данных.
     */
    @Override
    @Transactional
    public void updatedLastLogin(Person person) {
        updateLastLoginTimestamp(person);
        persistPerson(person);
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Устанавливает текущее время как дату последнего входа.
     */
    private void updateLastLoginTimestamp(Person person) {
        person.setLastLogin(LocalDateTime.now(MOSCOW_ZONE));
    }

    /**
     * Сохраняет изменения пользователя в базе данных.
     */
    private void persistPerson(Person person) {
        peopleRepository.save(person);
    }
}

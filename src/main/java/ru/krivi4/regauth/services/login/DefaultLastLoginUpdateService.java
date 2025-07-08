package ru.krivi4.regauth.services.login;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.repositories.PeopleRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**Фиксирует дату последнего входа пользователя в систему.*/
@Service
@RequiredArgsConstructor
public class DefaultLastLoginUpdateService implements LastLoginUpdateService {

  private final PeopleRepository peopleRepository;

  private static final ZoneId MOSCOW_ZONE   = ZoneId.of("Europe/Moscow");

  /**Обновляет поле lastLogin у пользователя и сохраняет сущность.*/
  @Override
  @Transactional
  public void updatedLastLogin(Person person) {
    person.setLastLogin(LocalDateTime.now(MOSCOW_ZONE));
    peopleRepository.save(person);
  }
}
package ru.krivi4.regauth.services.person;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.repositories.PeopleRepository;
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.web.exceptions.PersonUsernameNotFoundException;

/**Поиск пользователей по имени или телефону.*/
@Service
@RequiredArgsConstructor
public class DefaultPersonFindService implements PersonFindService {

  private final PeopleRepository peopleRepository;
  private final MessageService messageService;

  @Override
  @Transactional(readOnly = true)
  public Person findByUsername(String username) {
    return peopleRepository.findByUsername(username)
      .orElseThrow(() -> new PersonUsernameNotFoundException(messageService));
  }

  @Override
  @Transactional(readOnly = true)
  public Person findByPhoneNumber(String phoneNumber) {
    return peopleRepository.findByPhoneNumber(phoneNumber)
      .orElseThrow(() -> new PersonUsernameNotFoundException(messageService));
  }
}
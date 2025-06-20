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

import java.util.Optional;

/**
 * Адаптер Spring Security: загружает Person
 * из БД по имени пользователя и оборачивает в PersonDetails.
 */
@Service
@RequiredArgsConstructor
public class PersonDetailsService implements UserDetailsService {

  private final PeopleRepository peopleRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Person> person = peopleRepository.findByUsername(username);

    if(person.isEmpty()){
      throw new UsernameNotFoundException("Пользователь не найден");
    }
    return new PersonDetails(person.get());

  }

  @Transactional(readOnly = true)
  public boolean usernameExists(String username) {
    return peopleRepository.existsByUsername(username);
  }
}

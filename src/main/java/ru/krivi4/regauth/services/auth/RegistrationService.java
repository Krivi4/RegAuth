package ru.krivi4.regauth.services.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.repositories.PeopleRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Создаёт нового пользователя в бд people из данных, пришедших в Otp-токене регистрации.
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {

  private final PeopleRepository peopleRepository;
  private final PasswordEncoder passwordEncoder;

  /**Регистрирует пользователя и сохраняет его в БД.*/
  @Transactional
  public Person register(DecodedJWT decodedJWT) {
    Person person = new Person();
    String username = decodedJWT.getClaim("username").asString();
    String password = decodedJWT.getClaim("password").asString();
    String email = decodedJWT.getClaim("email").asString();
    String phoneNumber = decodedJWT.getClaim("phone_number").asString();

    person.setUsername(username);
    person.setPassword(passwordEncoder.encode(password));
    person.setEmail(email);
    person.setPhoneNumber(phoneNumber);
    person.setEnabled(true);
    person.setCreatedAt(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
    person.setLastLogin(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
    return peopleRepository.save(person);
  }
}

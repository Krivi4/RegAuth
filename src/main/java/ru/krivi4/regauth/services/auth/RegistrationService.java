package ru.krivi4.regauth.services.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.models.Role;
import ru.krivi4.regauth.repositories.PeopleRepository;
import ru.krivi4.regauth.repositories.RoleRepository;
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.utils.CredentialValidator;
import ru.krivi4.regauth.web.exceptions.DefaultRoleNotFoundException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

/**
 * Создаёт нового пользователя в БД из данных, полученных в JWT при регистрации.
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {

  private final PeopleRepository peopleRepository;
  private final PasswordEncoder passwordEncoder;
  private final CredentialValidator credentialValidator;
  private final RoleRepository roleRepository;
  private final MessageService messageService;

  private static final String USERNAME_CLAIM     = "username";
  private static final String PASSWORD_CLAIM     = "password";
  private static final String EMAIL_CLAIM        = "email";
  private static final String PHONE_NUMBER_CLAIM = "phone_number";

  private static final Locale DEFAULT_LOCALE = Locale.ROOT;
  private static final ZoneId MOSCOW_ZONE   = ZoneId.of("Europe/Moscow");
  private static final String      USER_ROLE_NAME = "USER";

  /**
   * Снимает данные из decodedJwt, собирает сущность Person,
   * сохраняет её в БД и возвращает.
   */
  @Transactional
  public Person register(DecodedJWT decodedJwt) {
    Person person = buildPersonFromJwt(decodedJwt);
    assignUserRole(person);
    return savePerson(person);
  }

  /* --------------вспомогательные методы --------------*/

  /**
   * Извлекает поля из decodedJwt, проверяет и кодирует пароль,
   * заполняет все поля нового Person.
   */
  private Person buildPersonFromJwt(DecodedJWT decodedJwt) {
    String rawUsername = decodedJwt.getClaim(USERNAME_CLAIM).asString();
    String rawPassword = decodedJwt.getClaim(PASSWORD_CLAIM).asString();
    String rawEmail    = decodedJwt.getClaim(EMAIL_CLAIM).asString();
    String rawPhone    = decodedJwt.getClaim(PHONE_NUMBER_CLAIM).asString();

    credentialValidator.isValidPassword(rawPassword);

    Person person = new Person();
    populatePersonFields(person, rawUsername, rawPassword, rawEmail, rawPhone);
    return person;
  }

  /**
   * Устанавливает в Person все поля: логин, пароль, e-mail, телефон,
   * признак enabled, дату создания и дату последнего входа.
   */
  private void populatePersonFields(
          Person person,
          String rawUsername,
          String rawPassword,
          String rawEmail,
          String rawPhone
  ) {
    person.setUsername(rawUsername.toLowerCase(DEFAULT_LOCALE));
    person.setPassword(passwordEncoder.encode(rawPassword));
    person.setEmail(rawEmail);
    person.setPhoneNumber(rawPhone);
    person.setEnabled(true);

    LocalDateTime now = LocalDateTime.now(MOSCOW_ZONE);
    person.setCreatedAt(now);
    person.setLastLogin(now);
  }

  /**
   * Сохраняет нового пользователя в таблицу people и возвращает результат.
   */
  private Person savePerson(Person person) {
    return peopleRepository.save(person);
  }

  /** Назначает пользователю базовую роль USER. */
  private void assignUserRole(Person person) {
    Role userRole = roleRepository.findByName(USER_ROLE_NAME)
            .orElseThrow(() -> new DefaultRoleNotFoundException(USER_ROLE_NAME, messageService));
    person.getRoles().add(userRole);
  }

}

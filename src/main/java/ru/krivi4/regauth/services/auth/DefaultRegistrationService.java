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
 * Сервис регистрации.
 * Создаёт нового пользователя в базе данных на основе данных из JWT.
 */
@Service
@RequiredArgsConstructor
public class DefaultRegistrationService implements RegistrationService {

    private static final String USERNAME_CLAIM = "username";
    private static final String PASSWORD_CLAIM = "password";
    private static final String EMAIL_CLAIM = "email";
    private static final String PHONE_NUMBER_CLAIM = "phone_number";
    private static final ZoneId MOSCOW_ZONE = ZoneId.of("Europe/Moscow");
    private static final String USER_ROLE_NAME = "USER";

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CredentialValidator credentialValidator;
    private final RoleRepository roleRepository;
    private final MessageService messageService;

    /**
     * Регистрирует нового пользователя:
     * - извлекает данные из JWT,
     * - создаёт сущность Person,
     * - назначает базовую роль USER,
     * - сохраняет пользователя в базе данных.
     */
    @Override
    @Transactional
    public Person register(DecodedJWT decodedJwt) {
        Person person = createPersonFromJwt(decodedJwt);
        assignDefaultUserRole(person);
        return persistPerson(person);
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Создаёт сущность Person на основе данных из JWT.
     */
    private Person createPersonFromJwt(DecodedJWT decodedJwt) {
        String username = extractUsername(decodedJwt);
        String password = extractPassword(decodedJwt);
        String email = extractEmail(decodedJwt);
        String phone = extractPhoneNumber(decodedJwt);

        validatePassword(password);

        return buildPerson(username, password, email, phone);
    }

    /**
     * Извлекает имя пользователя из JWT.
     */
    private String extractUsername(DecodedJWT decodedJwt) {
        return decodedJwt.getClaim(USERNAME_CLAIM).asString();
    }

    /**
     * Извлекает пароль пользователя из JWT.
     */
    private String extractPassword(DecodedJWT decodedJwt) {
        return decodedJwt.getClaim(PASSWORD_CLAIM).asString();
    }

    /**
     * Извлекает e-mail пользователя из JWT.
     */
    private String extractEmail(DecodedJWT decodedJwt) {
        return decodedJwt.getClaim(EMAIL_CLAIM).asString();
    }

    /**
     * Извлекает номер телефона пользователя из JWT.
     */
    private String extractPhoneNumber(DecodedJWT decodedJwt) {
        return decodedJwt.getClaim(PHONE_NUMBER_CLAIM).asString();
    }

    /**
     * Проверяет валидность пароля.
     */
    private void validatePassword(String rawPassword) {
        credentialValidator.isValidPassword(rawPassword);
    }

    /**
     * Создаёт и заполняет объект Person.
     */
    private Person buildPerson(String username, String password, String email, String phone) {
        Person person = new Person();
        populatePersonFields(person, username, password, email, phone);
        return person;
    }

    /**
     * Устанавливает значения полей для нового пользователя.
     */
    private void populatePersonFields(Person person, String username, String rawPassword, String email, String phone) {
        person.setUsername(username.toLowerCase(Locale.ROOT));
        person.setPassword(passwordEncoder.encode(rawPassword));
        person.setEmail(email);
        person.setPhoneNumber(phone);
        person.setEnabled(true);

        LocalDateTime now = LocalDateTime.now(MOSCOW_ZONE);
        person.setCreatedAt(now);
        person.setLastLogin(now);
    }

    /**
     * Назначает пользователю базовую роль USER.
     */
    private void assignDefaultUserRole(Person person) {
        Role userRole = findUserRole();
        person.getRoles().add(userRole);
    }

    /**
     * Ищет роль USER в базе данных или выбрасывает исключение.
     */
    private Role findUserRole() {
        return roleRepository.findByName(USER_ROLE_NAME)
                .orElseThrow(() -> new DefaultRoleNotFoundException(USER_ROLE_NAME, messageService));
    }

    /**
     * Сохраняет пользователя в базе данных и возвращает его.
     */
    private Person persistPerson(Person person) {
        return peopleRepository.save(person);
    }
}

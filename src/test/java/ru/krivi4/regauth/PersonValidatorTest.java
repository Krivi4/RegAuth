package ru.krivi4.regauth;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.services.auth.PersonDetailsService;
import ru.krivi4.regauth.util.PersonValidator;
import ru.krivi4.regauth.web.exceptions.DuplicatePersonException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Проверяем генерацию DuplicatePersonException,
 * когда имя уже занято.
 */
class PersonValidatorTest {

  /**
   * Если сервис сообщает, что username
   * уже существует, валидатор обязан выбросить исключение.
   */
  @Test
  void validate_shouldThrowDuplicatePersonException() {

    PersonDetailsService service = Mockito.mock(PersonDetailsService.class);
    when(service.usernameExists("busy")).thenReturn(true);

    PersonValidator validator = new PersonValidator(service);

    Person person = new Person();
    person.setUsername("busy");

    Errors errors = new BeanPropertyBindingResult(person, "person");

    assertThatThrownBy(() -> validator.validate(person, errors))
      .isInstanceOf(DuplicatePersonException.class);
  }
}
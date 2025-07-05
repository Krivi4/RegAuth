package ru.krivi4.regauth.util;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.services.auth.PersonDetailsService;
import ru.krivi4.regauth.web.exceptions.DuplicatePersonException;

/**Проверяет уникальность имени пользователя при регистрации.*/
@Component
@RequiredArgsConstructor
public class PersonValidator implements Validator {

  private final PersonDetailsService personDetailsService;

  /**Поддерживает класс Person.*/
  @Override
  public boolean supports(Class<?> clazz) {
    return Person.class.equals(clazz);
  }

  /**Бросает DuplicatePersonException, если username занят.*/
  @Override
  public void validate(Object target, Errors errors) {

    Person person = (Person) target;
    String username = person.getUsername();

    if(StringUtils.isEmpty(username)) {
      return;
    }

    if (personDetailsService.usernameExists(username)) {
      throw new DuplicatePersonException(username);
    }
  }
}

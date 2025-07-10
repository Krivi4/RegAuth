package ru.krivi4.regauth.services.person;

import ru.krivi4.regauth.models.Person;

/**
 * Сервис для поиска пользователей по имени или номеру телефона.
 */
public interface PersonFindService {

  /**
   * Находит пользователя по имени.
   */
  Person findByUsername(String username);

  /**
   * Находит пользователя по номеру телефона.
   */
  Person findByPhoneNumber(String phoneNumber);
}

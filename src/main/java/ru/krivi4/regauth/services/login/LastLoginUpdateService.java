package ru.krivi4.regauth.services.login;

import ru.krivi4.regauth.models.Person;

/**
 * Контракт сервиса для фиксации даты последнего входа.
 */
public interface LastLoginUpdateService {

  /**
   * Обновляет дату последнего входа пользователя.
   */
  void updatedLastLogin(Person person);
}

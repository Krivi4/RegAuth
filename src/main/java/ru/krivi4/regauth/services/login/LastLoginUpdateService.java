package ru.krivi4.regauth.services.login;

import ru.krivi4.regauth.models.Person;

/** Фиксирует дату последнего входа пользователя в систему.*/
public interface LastLoginUpdateService {

  void updatedLastLogin(Person person);
}
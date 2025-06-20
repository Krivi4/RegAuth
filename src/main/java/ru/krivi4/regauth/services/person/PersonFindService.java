package ru.krivi4.regauth.services.person;

import ru.krivi4.regauth.models.Person;
/**Поиск пользователей по имени или телефону.*/
public interface PersonFindService {
  Person findByUsername(String username);
  Person findByPhoneNumber(String phoneNumber);
}

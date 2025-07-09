package ru.krivi4.regauth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.krivi4.regauth.models.Person;

import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {

    /**
     * Находит пользователя по имени.
     */
    Optional<Person> findByUsername(String username);

    /**
     * Проверяет существование пользователя по имени.
     */
    boolean existsByUsername(String username);

    /**
     * Находит пользователя по номеру телефона.
     */
    Optional<Person> findByPhoneNumber(String phoneNumber);
}

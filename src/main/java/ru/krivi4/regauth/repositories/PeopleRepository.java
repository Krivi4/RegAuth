package ru.krivi4.regauth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.krivi4.regauth.models.Person;

import java.util.List;
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

    /**
     * Находит всех пользователей вместе с их ролями.
     */
    @Query("SELECT DISTINCT p FROM Person p LEFT JOIN FETCH p.roles")
    List<Person> findAllWithRoles();

}

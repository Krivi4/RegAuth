package ru.krivi4.regauth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.krivi4.regauth.models.Person;

import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {

  Optional<Person> findByUsername(String username);

  boolean existsByUsername(String username);

  Optional<Person> findByPhoneNumber(String phoneNumber);
}

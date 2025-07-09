package ru.krivi4.regauth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.krivi4.regauth.models.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    /**
     * Находит роль по имени.
     */
    Optional<Role> findByName(String name);
}

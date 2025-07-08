package ru.krivi4.regauth.views.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.krivi4.regauth.models.Person;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO «пользователь для админ-панели».
 */
@Getter
@AllArgsConstructor
public class UserView {

    private final String       username;
    private final String       email;
    private final String       phoneNumber;
    private final boolean      enabled;
    private final Set<String>  roles;

    public static UserView fromPerson(Person p) {
        return new UserView(
                p.getUsername(),
                p.getEmail(),
                p.getPhoneNumber(),
                p.isEnabled(),
                p.getRoles().stream()
                        .map(r -> r.getName())
                        .collect(Collectors.toSet())
        );
    }
}

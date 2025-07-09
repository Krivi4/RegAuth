package ru.krivi4.regauth.views;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * DTO «пользователь для админ-панели».
 */
@Getter
@AllArgsConstructor
public class AdminUserView {

    private final String username;
    private final String email;
    private final String phoneNumber;
    private final boolean enabled;
    private final Set<String> roles;

}

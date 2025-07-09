package ru.krivi4.regauth.views;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Ответ add/remove-role: echo username, role, message.
 */
@Getter
@AllArgsConstructor
public class RoleUpdateResponseView {
    private final String username;
    private final String role;
    private final String message;
}

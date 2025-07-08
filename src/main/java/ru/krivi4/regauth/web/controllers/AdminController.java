package ru.krivi4.regauth.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.krivi4.regauth.services.admin.AdminService;
import ru.krivi4.regauth.views.AdminActionResponseView;
import ru.krivi4.regauth.views.admin.RoleUpdateResponseView;
import ru.krivi4.regauth.views.admin.UserView;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер администрирования пользователей.
 * Доступен только для ROLE_ADMIN.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    /* ---------- users ---------- */

    /** Список всех пользователей. */
    @GetMapping(
            value    = "/users",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserView>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /** Вкл/выкл пользователя. */
    @PatchMapping(
            value    = "/users/{username}/enabled/{flag}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminActionResponseView> setEnabled(
            @PathVariable String username,
            @PathVariable boolean flag) {

        return ResponseEntity.ok(adminService.setUserEnabled(username, flag));
    }

    /** Удалить пользователя. */
    @DeleteMapping(
            value    = "/users/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminActionResponseView> deleteUser(
            @PathVariable String username) {

        return ResponseEntity.ok(adminService.deleteUser(username));
    }

    /** Добавить роль. */
    @PostMapping(
            value    = "/users/{username}/roles/{role}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleUpdateResponseView> addRole(
            @PathVariable String username,
            @PathVariable String role) {

        return ResponseEntity.ok(adminService.addRoleToUser(username, role));
    }

    /** Удалить роль. */
    @DeleteMapping(
            value    = "/users/{username}/roles/{role}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleUpdateResponseView> removeRole(
            @PathVariable String username,
            @PathVariable String role) {

        return ResponseEntity.ok(adminService.removeRoleFromUser(username, role));
    }


    /** Revoke / restore refresh-токен. */
    @PatchMapping(
            value    = "/tokens/{jti}/revoked/{flag}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminActionResponseView> setRefreshRevoked(
            @PathVariable UUID jti,
            @PathVariable boolean flag) {

        return ResponseEntity.ok(adminService.setRefreshRevoked(jti, flag));
    }
}

package ru.krivi4.regauth.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.krivi4.regauth.services.admin.AdminService;
import ru.krivi4.regauth.views.AdminActionResponseView;
import ru.krivi4.regauth.views.AdminUserView;
import ru.krivi4.regauth.views.RoleUpdateResponseView;

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

    private static final String USERS = "/users";
    private static final String USER = USERS + "/{username}";
    private static final String USER_ENABLED = USER + "/enabled/{flag}";
    private static final String USER_ROLE = USER + "/roles/{role}";
    private static final String TOKEN = "/tokens/{jti}/revoked/{flag}";

    private final AdminService adminService;

    /**
     * Список всех пользователей.
     */
    @GetMapping(value = USERS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AdminUserView>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Вкл/выкл пользователя.
     */
    @PatchMapping(value = USER_ENABLED, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminActionResponseView> setEnabled(
            @PathVariable String username,
            @PathVariable boolean flag) {

        return ResponseEntity.ok(adminService.setUserEnabled(username, flag));
    }

    /**
     * Удалить пользователя.
     */
    @DeleteMapping(value = USER, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminActionResponseView> deleteUser(
            @PathVariable String username) {

        return ResponseEntity.ok(adminService.deleteUser(username));
    }

    /**
     * Добавить роль пользователю.
     */
    @PostMapping(value = USER_ROLE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleUpdateResponseView> addRole(
            @PathVariable String username,
            @PathVariable String role) {

        return ResponseEntity.ok(adminService.addRoleToUser(username, role));
    }

    /**
     * Удалить роль у пользователя.
     */
    @DeleteMapping(value = USER_ROLE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleUpdateResponseView> removeRole(
            @PathVariable String username,
            @PathVariable String role) {

        return ResponseEntity.ok(adminService.removeRoleFromUser(username, role));
    }

    /**
     * Заблокировать / разблокировать refresh-токен.
     */
    @PatchMapping(value = TOKEN, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminActionResponseView> setRefreshRevoked(
            @PathVariable UUID jti,
            @PathVariable boolean flag) {

        return ResponseEntity.ok(adminService.setRefreshRevoked(jti, flag));
    }
}

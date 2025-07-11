package ru.krivi4.regauth.web.controllers.admin;

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

import static ru.krivi4.regauth.web.controllers.admin.AdminUserController.API_BASE;

/**
 * REST-контроллер для управления пользователями (админ-панель).
 * Все методы доступны только пользователям с ролью ADMIN.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(API_BASE)
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    public static final String API_BASE = "/regauth/api/v1/auth";

    private final AdminService adminService;

    /**
     * Возвращает список всех пользователей.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AdminUserView>> getAllUsers() {
        return ResponseEntity.ok(fetchAllUsers());
    }

    /**
     * Включает или выключает пользователя.
     */
    @PatchMapping(value = "/{username}/enabled/{flag}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminActionResponseView> setEnabled(
            @PathVariable String username,
            @PathVariable boolean flag) {
        return ResponseEntity.ok(updateUserEnabled(username, flag));
    }

    /**
     * Удаляет пользователя из системы.
     */
    @DeleteMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminActionResponseView> deleteUser(@PathVariable String username) {
        return ResponseEntity.ok(processUserDeletion(username));
    }

    /**
     * Добавляет роль пользователю.
     */
    @PostMapping(value = "/{username}/roles/{role}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleUpdateResponseView> addRole(
            @PathVariable String username,
            @PathVariable String role) {
        return ResponseEntity.ok(assignRoleToUser(username, role));
    }

    /**
     * Удаляет роль у пользователя.
     */
    @DeleteMapping(value = "/{username}/roles/{role}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoleUpdateResponseView> removeRole(
            @PathVariable String username,
            @PathVariable String role) {
        return ResponseEntity.ok(removeRoleFromUser(username, role));
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Загружает список всех пользователей через сервис.
     */
    private List<AdminUserView> fetchAllUsers() {
        return adminService.getAllUsers();
    }

    /**
     * Обновляет статус активности пользователя (enabled/disabled).
     */
    private AdminActionResponseView updateUserEnabled(String username, boolean enabled) {
        return adminService.setUserEnabled(username, enabled);
    }

    /**
     * Удаляет пользователя через сервис администрирования.
     */
    private AdminActionResponseView processUserDeletion(String username) {
        return adminService.deleteUser(username);
    }

    /**
     * Добавляет указанную роль пользователю.
     */
    private RoleUpdateResponseView assignRoleToUser(String username, String role) {
        return adminService.addRoleToUser(username, role);
    }

    /**
     * Удаляет указанную роль у пользователя.
     */
    private RoleUpdateResponseView removeRoleFromUser(String username, String role) {
        return adminService.removeRoleFromUser(username, role);
    }
}

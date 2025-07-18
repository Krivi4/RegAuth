package ru.krivi4.regauth.web.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.krivi4.regauth.services.admin.AdminService;
import ru.krivi4.regauth.views.AdminActionResponseView;

import java.util.UUID;

import static ru.krivi4.regauth.web.controllers.admin.AdminTokenController.API_BASE;

/**
 * REST-контроллер для управления токенами (админ-панель).
 * Все методы доступны только пользователям с ролью ADMIN.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(API_BASE)
@PreAuthorize("hasRole('ADMIN')")
public class AdminTokenController {

    public static final String API_BASE = "/regauth/api/v1/auth";

    private final AdminService adminService;

    /**
     * Блокирует или разблокирует refresh-токен по его идентификатору.
     */
    @PatchMapping(value = "/{jti}/revoked/{flag}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminActionResponseView> setRefreshRevoked(
            @PathVariable UUID jti,
            @PathVariable boolean flag) {
        return ResponseEntity.ok(updateRefreshTokenStatus(jti, flag));
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Обновляет статус revoked для указанного refresh-токена.
     */
    private AdminActionResponseView updateRefreshTokenStatus(UUID jti, boolean revoked) {
        return adminService.setRefreshRevoked(jti, revoked);
    }
}

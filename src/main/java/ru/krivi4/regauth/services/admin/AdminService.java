package ru.krivi4.regauth.services.admin;

import ru.krivi4.regauth.views.AdminActionResponseView;
import ru.krivi4.regauth.views.AdminUserView;
import ru.krivi4.regauth.views.RoleUpdateResponseView;

import java.util.List;
import java.util.UUID;

/**
 * Контракт сервиса-администратора:
 * управление пользователями, ролями и токенами.
 */
public interface AdminService {

    /**
     * Возвращает список всех пользователей в формате AdminUserView.
     */
    List<AdminUserView> getAllUsers();

    /**
     * Добавляет роль пользователю по его имени и имени роли.
     */
    RoleUpdateResponseView addRoleToUser(String username, String roleName);

    /**
     * Удаляет роль у пользователя по его имени и имени роли.
     */
    RoleUpdateResponseView removeRoleFromUser(String username, String roleName);

    /**
     * Активирует или блокирует пользователя по его имени.
     */
    AdminActionResponseView setUserEnabled(String username, boolean enabled);

    /**
     * Блокирует или разблокирует refresh-токен по идентификатору токена.
     */
    AdminActionResponseView setRefreshRevoked(UUID jti, boolean revoked);

    /**
     * Полностью удаляет пользователя по его имени.
     */
    AdminActionResponseView deleteUser(String username);
}

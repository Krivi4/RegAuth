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

    List<AdminUserView> getAllUsers();

    RoleUpdateResponseView addRoleToUser(String username, String roleName);

    RoleUpdateResponseView removeRoleFromUser(String username, String roleName);

    AdminActionResponseView setUserEnabled(String username, boolean enabled);

    AdminActionResponseView setRefreshRevoked(UUID jti, boolean revoked);

    AdminActionResponseView deleteUser(String username);
}

package ru.krivi4.regauth.services.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.adapters.mappers.AdminUserViewMapper;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.models.RefreshToken;
import ru.krivi4.regauth.models.Role;
import ru.krivi4.regauth.repositories.PeopleRepository;
import ru.krivi4.regauth.repositories.RefreshTokenRepository;
import ru.krivi4.regauth.repositories.RoleRepository;
import ru.krivi4.regauth.services.message.DefaultMessageService;
import ru.krivi4.regauth.views.AdminActionResponseView;
import ru.krivi4.regauth.views.RoleUpdateResponseView;
import ru.krivi4.regauth.views.AdminUserView;
import ru.krivi4.regauth.web.exceptions.DefaultRoleNotFoundException;
import ru.krivi4.regauth.web.exceptions.PersonUsernameNotFoundException;
import ru.krivi4.regauth.web.exceptions.RefreshTokenNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Реализация сервиса-администратора:
 * управление пользователями, ролями и токенами.
 */
@Service
@RequiredArgsConstructor
public class DefaultAdminService implements AdminService {

    private static final String ROLE_ADDED_MSG_KEY     = "admin.role.added.success";
    private static final String ROLE_REMOVED_MSG_KEY   = "admin.role.removed.success";
    private static final String USER_DELETED_MSG_KEY   = "admin.user.deleted.success";
    private static final String USER_STATUS_MSG_KEY    = "admin.user.status.updated";
    private static final String REFRESH_STATUS_MSG_KEY = "admin.refresh.status.updated";

    private static final String STATUS_ENABLED  = "enabled";
    private static final String STATUS_DISABLED = "disabled";
    private static final String STATUS_REVOKED  = "revoked";
    private static final String STATUS_ACTIVE   = "active";

    private static final boolean READ_ONLY = true;

    private final PeopleRepository peopleRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final DefaultMessageService defaultMessageService;
    private final AdminUserViewMapper adminUserViewMapper;

    /** Возвращает список всех пользователей (DTO). */
    @Override
    @Transactional(readOnly = READ_ONLY)
    public List<AdminUserView> getAllUsers() {
        return adminUserViewMapper.toViewList(peopleRepository.findAll());
    }

    /** Добавляет роль пользователю. */
    @Override
    @Transactional
    public RoleUpdateResponseView addRoleToUser(String username, String roleName) {
        Person person = getPerson(username);
        Role role = getRole(roleName);

        person.getRoles().add(role);
        peopleRepository.save(person);

        return buildRoleUpdateResponse(ROLE_ADDED_MSG_KEY, username, roleName);
    }

    /** Удаляет роль у пользователя. */
    @Override
    @Transactional
    public RoleUpdateResponseView removeRoleFromUser(String username, String roleName) {
        Person person = getPerson(username);
        Role role = getRole(roleName);

        person.getRoles().remove(role);
        peopleRepository.save(person);

        return buildRoleUpdateResponse(ROLE_REMOVED_MSG_KEY, username, roleName);
    }

    /** Активирует или блокирует пользователя. */
    @Override
    @Transactional
    public AdminActionResponseView setUserEnabled(String username, boolean enabled) {
        Person person = getPerson(username);
        person.setEnabled(enabled);
        peopleRepository.save(person);

        String status = enabled ? STATUS_ENABLED : STATUS_DISABLED;
        return buildActionResponse(USER_STATUS_MSG_KEY, username, status);
    }

    /** Блокирует или разблокирует refresh-токен. */
    @Override
    @Transactional
    public AdminActionResponseView setRefreshRevoked(UUID jti, boolean revoked) {
        RefreshToken token = getRefreshToken(jti);
        token.setRevoked(revoked);
        refreshTokenRepository.save(token);

        String status = revoked ? STATUS_REVOKED : STATUS_ACTIVE;
        return buildActionResponse(REFRESH_STATUS_MSG_KEY, jti.toString(), status);
    }

    /** Полностью удаляет пользователя. */
    @Override
    @Transactional
    public AdminActionResponseView deleteUser(String username) {
        Person person = getPerson(username);
        peopleRepository.delete(person);
        return buildActionResponse(USER_DELETED_MSG_KEY, username);
    }

    /* ---------- вспомогательные методы-------------- */

    private Person getPerson(String username) {
        return peopleRepository.findByUsername(username)
                .orElseThrow(() -> new PersonUsernameNotFoundException(defaultMessageService));
    }

    private Role getRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new DefaultRoleNotFoundException(roleName, defaultMessageService));
    }

    private RefreshToken getRefreshToken(UUID jti) {
        return refreshTokenRepository.findById(jti)
                .orElseThrow(() -> new RefreshTokenNotFoundException(jti.toString(), defaultMessageService));
    }

    private RoleUpdateResponseView buildRoleUpdateResponse(String msgKey, String username, String roleName) {
        String message = defaultMessageService.getMessage(msgKey, username, roleName);
        return new RoleUpdateResponseView(username, roleName, message);
    }

    private AdminActionResponseView buildActionResponse(String msgKey, Object... args) {
        String message = defaultMessageService.getMessage(msgKey, args);
        return new AdminActionResponseView(message);
    }
}

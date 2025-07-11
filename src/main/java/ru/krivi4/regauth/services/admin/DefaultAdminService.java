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
import ru.krivi4.regauth.services.message.MessageService;
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
    private final MessageService messageService;
    private final AdminUserViewMapper adminUserViewMapper;

    /** Возвращает список всех пользователей в формате AdminUserView. */
    @Override
    @Transactional(readOnly = READ_ONLY)
    public List<AdminUserView> getAllUsers() {
        List<Person> users = loadAllUsers();
        return mapUsersToView(users);
    }

    /** Добавляет роль пользователю и возвращает результат операции. */
    @Override
    @Transactional
    public RoleUpdateResponseView addRoleToUser(String username, String roleName) {
        Person person = loadPerson(username);
        Role role = loadRole(roleName);

        addRole(person, role);
        savePerson(person);

        return buildRoleUpdateResponse(ROLE_ADDED_MSG_KEY, username, roleName);
    }

    /** Удаляет роль у пользователя и возвращает результат операции. */
    @Override
    @Transactional
    public RoleUpdateResponseView removeRoleFromUser(String username, String roleName) {
        Person person = loadPerson(username);
        Role role = loadRole(roleName);

        removeRole(person, role);
        savePerson(person);

        return buildRoleUpdateResponse(ROLE_REMOVED_MSG_KEY, username, roleName);
    }

    /** Активирует или блокирует пользователя и возвращает результат операции. */
    @Override
    @Transactional
    public AdminActionResponseView setUserEnabled(String username, boolean enabled) {
        Person person = loadPerson(username);

        updateUserEnabledStatus(person, enabled);
        savePerson(person);

        String status = resolveUserStatus(enabled);
        return buildActionResponse(USER_STATUS_MSG_KEY, username, status);
    }

    /** Блокирует или разблокирует refresh-токен и возвращает результат операции. */
    @Override
    @Transactional
    public AdminActionResponseView setRefreshRevoked(UUID jti, boolean revoked) {
        RefreshToken token = loadRefreshToken(jti);

        updateRefreshTokenRevokedStatus(token, revoked);
        saveRefreshToken(token);

        String status = resolveRefreshStatus(revoked);
        return buildActionResponse(REFRESH_STATUS_MSG_KEY, jti.toString(), status);
    }

    /** Полностью удаляет пользователя и возвращает результат операции. */
    @Override
    @Transactional
    public AdminActionResponseView deleteUser(String username) {
        Person person = loadPerson(username);

        deletePerson(person);
        return buildActionResponse(USER_DELETED_MSG_KEY, username);
    }

    //*----------Вспомогательные методы----------*//

    /**
     * Загружает всех пользователей из базы данных.
     */
    private List<Person> loadAllUsers() {
        return peopleRepository.findAllWithRoles();
    }

    /**
     * Преобразует список пользователей в AdminUserView.
     */
    private List<AdminUserView> mapUsersToView(List<Person> users) {
        return adminUserViewMapper.toViewList(users);
    }

    /**
     * Загружает пользователя по имени или бросает исключение.
     */
    private Person loadPerson(String username) {
        return peopleRepository.findByUsername(username)
                .orElseThrow(() -> new PersonUsernameNotFoundException(messageService));
    }

    /**
     * Загружает роль по имени или бросает исключение.
     */
    private Role loadRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new DefaultRoleNotFoundException(roleName, messageService));
    }

    /**
     * Загружает refresh-токен по идентификатору или бросает исключение.
     */
    private RefreshToken loadRefreshToken(UUID jti) {
        return refreshTokenRepository.findById(jti)
                .orElseThrow(() -> new RefreshTokenNotFoundException(jti.toString(), messageService));
    }

    /**
     * Добавляет роль пользователю.
     */
    private void addRole(Person person, Role role) {
        person.getRoles().add(role);
    }

    /**
     * Удаляет роль у пользователя.
     */
    private void removeRole(Person person, Role role) {
        person.getRoles().remove(role);
    }

    /**
     * Обновляет статус активности пользователя.
     */
    private void updateUserEnabledStatus(Person person, boolean enabled) {
        person.setEnabled(enabled);
    }

    /**
     * Обновляет статус заблокированности refresh-токена.
     */
    private void updateRefreshTokenRevokedStatus(RefreshToken token, boolean revoked) {
        token.setRevoked(revoked);
    }

    /**
     * Определяет статус пользователя по флагу активности.
     */
    private String resolveUserStatus(boolean enabled) {
        return enabled ? STATUS_ENABLED : STATUS_DISABLED;
    }

    /**
     * Определяет статус refresh-токена по флагу блокировки.
     */
    private String resolveRefreshStatus(boolean revoked) {
        return revoked ? STATUS_REVOKED : STATUS_ACTIVE;
    }

    /**
     * Сохраняет пользователя в базе данных.
     */
    private void savePerson(Person person) {
        peopleRepository.save(person);
    }

    /**
     * Сохраняет refresh-токен в базе данных.
     */
    private void saveRefreshToken(RefreshToken token) {
        refreshTokenRepository.save(token);
    }

    /**
     * Удаляет пользователя из базы данных.
     */
    private void deletePerson(Person person) {
        peopleRepository.delete(person);
    }

    /**
     * Строит ответ о результате изменения ролей пользователя.
     */
    private RoleUpdateResponseView buildRoleUpdateResponse(String msgKey, String username, String roleName) {
        String message = messageService.getMessage(msgKey, username, roleName);
        return new RoleUpdateResponseView(username, roleName, message);
    }

    /**
     * Строит ответ о результате действия администратора.
     */
    private AdminActionResponseView buildActionResponse(String msgKey, Object... args) {
        String message = messageService.getMessage(msgKey, args);
        return new AdminActionResponseView(message);
    }
}

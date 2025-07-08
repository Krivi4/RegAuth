package ru.krivi4.regauth.services.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.models.RefreshToken;
import ru.krivi4.regauth.models.Role;
import ru.krivi4.regauth.repositories.PeopleRepository;
import ru.krivi4.regauth.repositories.RefreshTokenRepository;
import ru.krivi4.regauth.repositories.RoleRepository;
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.views.AdminActionResponseView;
import ru.krivi4.regauth.views.admin.RoleUpdateResponseView;
import ru.krivi4.regauth.views.admin.UserView;
import ru.krivi4.regauth.web.exceptions.DefaultRoleNotFoundException;
import ru.krivi4.regauth.web.exceptions.PersonUsernameNotFoundException;
import ru.krivi4.regauth.web.exceptions.RefreshTokenNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис-администратор: управление пользователями, ролями и токенами.
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final PeopleRepository       peopleRepository;
    private final RoleRepository         roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MessageService         messageService;

    private static final String ROLE_ADDED_MSG_KEY        = "admin.role.added.success";
    private static final String ROLE_REMOVED_MSG_KEY      = "admin.role.removed.success";
    private static final String USER_DELETED_MSG_KEY      = "admin.user.deleted.success";
    private static final String USER_STATUS_MSG_KEY       = "admin.user.status.updated";
    private static final String REFRESH_STATUS_MSG_KEY    = "admin.refresh.status.updated";

    /** Возвращает список всех пользователей (DTO). */
    @Transactional(readOnly = true)
    public List<UserView> getAllUsers() {
        return peopleRepository.findAll().stream()
                .map(UserView::fromPerson)
                .collect(Collectors.toList());
    }

    /** Добавляет роль пользователю и возвращает DTO-ответ. */
    @Transactional
    public RoleUpdateResponseView addRoleToUser(String username, String roleName) {
        Person person = getPerson(username);
        Role role = getRole(roleName);

        person.getRoles().add(role);
        peopleRepository.save(person);

        return buildRoleUpdateResponse(ROLE_ADDED_MSG_KEY, username, roleName);
    }

    /** Удаляет роль у пользователя и возвращает DTO-ответ. */
    @Transactional
    public RoleUpdateResponseView removeRoleFromUser(String username, String roleName) {
        Person person = getPerson(username);
        Role role = getRole(roleName);

        person.getRoles().remove(role);
        peopleRepository.save(person);

        return buildRoleUpdateResponse(ROLE_REMOVED_MSG_KEY, username, roleName);
    }

    /** Активирует или блокирует пользователя (enabled true/false). */
    @Transactional
    public AdminActionResponseView setUserEnabled(String username, boolean enabled) {
        Person person = getPerson(username);
        person.setEnabled(enabled);
        peopleRepository.save(person);

        String status = enabled ? "enabled" : "disabled";
        return buildActionResponse(USER_STATUS_MSG_KEY, username, status);
    }

    /** Блокирует или разблокирует refresh-токен (revoked true/false). */
    @Transactional
    public AdminActionResponseView setRefreshRevoked(UUID jti, boolean revoked) {
        RefreshToken token = getRefreshToken(jti);
        token.setRevoked(revoked);
        refreshTokenRepository.save(token);

        String status = revoked ? "revoked" : "active";
        return buildActionResponse(REFRESH_STATUS_MSG_KEY, jti.toString(), status);
    }

    /** Полностью удаляет пользователя по username. */
    @Transactional
    public AdminActionResponseView deleteUser(String username) {
        Person person = getPerson(username);
        peopleRepository.delete(person);

        return buildActionResponse(USER_DELETED_MSG_KEY, username);
    }

    /* -------------------- Вспомогательные методы -------------------- */

    /** Возвращает пользователя или выбрасывает исключение. */
    private Person getPerson(String username) {
        return peopleRepository.findByUsername(username)
                .orElseThrow(() -> new PersonUsernameNotFoundException(messageService));
    }

    /** Возвращает роль или выбрасывает исключение. */
    private Role getRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new DefaultRoleNotFoundException(roleName, messageService));
    }

    /** Возвращает refresh-токен или выбрасывает исключение. */
    private RefreshToken getRefreshToken(UUID jti) {
        return refreshTokenRepository.findById(jti)
                .orElseThrow(() -> new RefreshTokenNotFoundException(jti.toString(), messageService));
    }

    /** Строит DTO-ответ после обновления роли. */
    private RoleUpdateResponseView buildRoleUpdateResponse(String msgKey, String username, String roleName) {
        String message = messageService.getMessage(msgKey, username, roleName);
        return new RoleUpdateResponseView(username, roleName, message);
    }

    /** Строит простой DTO-ответ с текстовым сообщением. */
    private AdminActionResponseView buildActionResponse(String msgKey, Object... args) {
        String message = messageService.getMessage(msgKey, args);
        return new AdminActionResponseView(message);
    }
}

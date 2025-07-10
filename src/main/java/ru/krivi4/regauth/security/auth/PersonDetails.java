package ru.krivi4.regauth.security.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.krivi4.regauth.models.Person;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация UserDetails для сущности Person.
 * Предоставляет информацию о пользователе для Spring Security.
 */
@RequiredArgsConstructor
@Getter
public class PersonDetails implements UserDetails {

    private static final String ROLE_PREFIX = "ROLE_";

    private final Person person;

    /**
     * Возвращает коллекцию ролей пользователя в формате GrantedAuthority.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return mapRolesToAuthorities();
    }

    /**
     * Возвращает захешированный пароль пользователя.
     */
    @Override
    public String getPassword() {
        return extractPassword();
    }

    /**
     * Возвращает уникальное имя пользователя.
     */
    @Override
    public String getUsername() {
        return extractUsername();
    }

    /**
     * Проверяет, истёк ли аккаунт.
     * По умолчанию всегда возвращает true (аккаунт не истёк).
     */
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired();
    }

    /**
     * Проверяет, заблокирован ли аккаунт.
     * По умолчанию всегда возвращает true (аккаунт не заблокирован).
     */
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked();
    }

    /**
     * Проверяет, истекли ли учётные данные.
     * По умолчанию всегда возвращает true (учётные данные не истекли).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired();
    }

    /**
     * Проверяет, активен ли аккаунт пользователя.
     * Возвращает статус активности из поля enabled сущности Person.
     */
    @Override
    public boolean isEnabled() {
        return checkUserEnabled();
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Преобразует роли пользователя в коллекцию GrantedAuthority с префиксом ROLE_.
     */
    private Set<GrantedAuthority> mapRolesToAuthorities() {
        return person.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.getName()))
                .collect(Collectors.toSet());
    }

    /**
     * Извлекает зашифрованный пароль из сущности Person.
     */
    private String extractPassword() {
        return this.person.getPassword();
    }

    /**
     * Извлекает имя пользователя из сущности Person.
     */
    private String extractUsername() {
        return this.person.getUsername();
    }

    /**
     * Проверяет, истёк ли аккаунт. По умолчанию возвращает true.
     */
    private boolean accountNonExpired() {
        return true;
    }

    /**
     * Проверяет, заблокирован ли аккаунт. По умолчанию возвращает true.
     */
    private boolean accountNonLocked() {
        return true;
    }

    /**
     * Проверяет, истекли ли учётные данные. По умолчанию возвращает true.
     */
    private boolean credentialsNonExpired() {
        return true;
    }

    /**
     * Проверяет, активен ли аккаунт. Извлекает значение поля enabled у Person.
     */
    private boolean checkUserEnabled() {
        return this.person.isEnabled();
    }
}

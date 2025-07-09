package ru.krivi4.regauth.security.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.krivi4.regauth.models.Person;

import java.util.Collection;
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
        return person.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.getName()))
                .collect(Collectors.toSet());
    }

    /**
     * Возвращает захешированный пароль пользователя.
     */
    @Override
    public String getPassword() {
        return this.person.getPassword();
    }

    /**
     * Возвращает уникальное имя пользователя.
     */
    @Override
    public String getUsername() {
        return this.person.getUsername();
    }

    /**
     * Проверяет, истёк ли аккаунт.
     * По умолчанию всегда возвращает true (аккаунт не истёк).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Проверяет, заблокирован ли аккаунт.
     * По умолчанию всегда возвращает true (аккаунт не заблокирован).
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Проверяет, истекли ли учётные данные.
     * По умолчанию всегда возвращает true (учётные данные не истекли).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Проверяет, активен ли аккаунт пользователя.
     * Возвращает статус активности из поля enabled сущности Person.
     */
    @Override
    public boolean isEnabled() {
        return this.person.isEnabled();
    }
}

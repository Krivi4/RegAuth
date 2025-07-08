package ru.krivi4.regauth.security.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.krivi4.regauth.models.Person;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**Реализация UserDetails для сущности Person.*/
@RequiredArgsConstructor
@Getter
public class PersonDetails implements UserDetails {

  private static final String ROLE_PREFIX = "ROLE_";

  private final Person person;

  /**Возвращает роли/привилегии пользователя (в проекте отсутствуют роли и привилегии).*/
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return person.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.getName()))
            .collect(Collectors.toSet());
  }

  /**Возвращает захешированный пароль пользователя.*/
  @Override
  public String getPassword() {
    return this.person.getPassword();
  }

  /**Возвращает уникальное имя пользователя.*/
  @Override
  public String getUsername() {
    return this.person.getUsername();
  }

  /**По умолчанию возвращает что аккаунт не истёк*/
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**По умолчанию возвращает что аккаунт не заблокирован*/
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**По умолчанию возвращает что учётные данные не истекают*/
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**Возвращает статус активности аккаунта.*/
  @Override
  public boolean isEnabled() {
    return this.person.isEnabled();
  }
}

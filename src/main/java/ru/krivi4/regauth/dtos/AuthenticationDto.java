package ru.krivi4.regauth.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**DTO для входа пользователя: логин и пароль.*/
@Getter
@Setter
public class AuthenticationDto {

  @NotNull(message = "Введите имя пользователя")
  private String username;

  @NotNull(message = "Введите пароль")
  private String password;
}

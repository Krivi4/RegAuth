package ru.krivi4.regauth.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**DTO для регистрации пользователя.*/
@Getter
@Setter
public class PersonDto {

  @NotNull(message = "Введите имя пользователя")
  private String username;

  @NotNull(message = "Введите пароль")
  private String password;

  @NotEmpty(message = "Введите электронную почту")
  @Email(message = "Введите верный формат Email")
  private String email;

  @NotNull(message = "Введите номер телефона")
  private String phoneNumber;
}

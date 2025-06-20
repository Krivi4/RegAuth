package ru.krivi4.regauth.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "people")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"password", "email"})
public class Person {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "username")
  @NotNull(message = "Введите имя пользователя")
  private String username;

  @Column(name = "password")
  @NotNull(message = "Введите пароль")
  private String password;

  @Column(name = "email")
  @NotEmpty(message = "Введите e-mail")
  @Email(message = "Введите верный формат электронный почты")
  private String email;

  @Column(name = "phone_number")
  @NotNull(message = "Введите номер телефона")
  private String phoneNumber;

  /** Статус активности. */
  @Column(name = "enabled")
  private boolean enabled;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "last_login")
  private LocalDateTime lastLogin;
}

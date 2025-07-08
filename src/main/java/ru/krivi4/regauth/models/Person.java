package ru.krivi4.regauth.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"password", "email"})
public class Person {

  @Id
  @Column(name = "id", updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
  @SequenceGenerator(
          name = "user_seq_gen",
          sequenceName = "security.users_id_seq",
          allocationSize = 1
  )
  private int id;

  @NotBlank(message = "Введите имя пользователя")
  @Column(name = "username", nullable = false, unique = true, length = 100)
  private String username;

  @NotBlank(message = "Введите пароль")
  @Column(name = "password", nullable = false, length = 100)
  private String password;

  @NotBlank(message = "Введите e-mail")
  @Email(message = "Введите верный формат электронной почты")
  @Column(name = "email", nullable = false, length = 100)
  private String email;

  @NotBlank(message = "Введите номер телефона")
  @Column(name = "phone_number", nullable = false, unique = true, length = 20)
  private String phoneNumber;

  /**
   * Статус активности.
   */
  @Column(name = "enabled", nullable = false)
  private boolean enabled = false;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "last_login")
  private LocalDateTime lastLogin;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
          name = "person_role",
          schema = "security",
          joinColumns = @JoinColumn(name = "person_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  private Set<Role> roles = new HashSet<>();
}
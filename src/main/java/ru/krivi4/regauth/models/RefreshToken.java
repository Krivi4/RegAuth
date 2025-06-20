package ru.krivi4.regauth.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

  /** JTI токена. */
  @Id
  @Column(name = "jti")
  private UUID jti;

  @Column(name = "username", nullable = false)
  private String username;

  /** Дата истечения. */
  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  /** Статус отзыва. */
  @Column(name = "revoked", nullable = false)
  private boolean revoked;
}
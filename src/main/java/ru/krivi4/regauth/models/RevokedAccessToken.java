package ru.krivi4.regauth.models;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "revoked_access_tokens")
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class RevokedAccessToken {

  /** JTI токена. */
  @Id
  @Column(name = "jti")
  private UUID jti;

  /** Дата истечения (для очистки). */
  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;
}

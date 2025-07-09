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
@Table(name = RefreshToken.TABLE_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

  protected static final String TABLE_NAME = "refresh_tokens";
  private static final String COLUMN_JTI = "jti";
  private static final String COLUMN_USERNAME = "username";
  private static final String COLUMN_EXPIRES_AT = "expires_at";
  private static final String COLUMN_REVOKED = "revoked";
  private static final boolean NULLABLE = false;

  /** JTI токена. */
  @Id
  @Column(name = COLUMN_JTI)
  private UUID jti;

  @Column(name = COLUMN_USERNAME, nullable = NULLABLE)
  private String username;

  /** Дата истечения. */
  @Column(name = COLUMN_EXPIRES_AT, nullable = NULLABLE)
  private LocalDateTime expiresAt;

  /** Статус отзыва. */
  @Column(name = COLUMN_REVOKED, nullable = NULLABLE)
  private boolean revoked;
}

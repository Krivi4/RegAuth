package ru.krivi4.regauth.models;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = RevokedAccessToken.TABLE_NAME)
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class RevokedAccessToken {

    protected static final String TABLE_NAME = "revoked_access_tokens";
    private static final String COLUMN_JTI = "jti";
    private static final String COLUMN_EXPIRES_AT = "expires_at";
    private static final boolean NULLABLE = false;

    /**
     * JTI токена.
     */
    @Id
    @Column(name = COLUMN_JTI)
    private UUID jti;

    /**
     * Дата истечения (для очистки).
     */
    @Column(name = COLUMN_EXPIRES_AT, nullable = NULLABLE)
    private LocalDateTime expiresAt;
}

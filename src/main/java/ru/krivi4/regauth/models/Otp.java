package ru.krivi4.regauth.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = Otp.TABLE_NAME)
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class Otp {

    protected static final String TABLE_NAME = "otp_codes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_CODE_HASH = "code_hash";
    private static final String COLUMN_EXPIRES_AT = "expires_at";
    private static final String COLUMN_ATTEMPTS = "attempts";

    /**
     * Уникальный идентификатор Otp.
     */
    @Id
    @Column(name = COLUMN_ID)
    private UUID idOtp;

    /**
     * Номер телефона, связанный с кодом.
     */
    @Column(name = COLUMN_PHONE_NUMBER)
    private String phoneNumber;

    /**
     * Хэшированный код подтверждения.
     */
    @Column(name = COLUMN_CODE_HASH)
    private String codeHash;

    /**
     * Дата и время истечения.
     */
    @Column(name = COLUMN_EXPIRES_AT)
    private LocalDateTime expiresAtOTP;

    /**
     * Число попыток ввода.
     */
    @Column(name = COLUMN_ATTEMPTS)
    private int attempts;
}

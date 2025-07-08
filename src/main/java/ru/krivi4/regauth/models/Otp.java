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
@Table(name = "otp_codes")
@Getter @Setter
@Accessors(chain = true)
@NoArgsConstructor
public class Otp {

  /** Уникальный идентификатор Otp. */
  @Id
  @Column(name = "id")
  private UUID idOtp;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "code_hash")
  private String codeHash;

  /** Дата и время истечения. */
  @Column(name = "expires_at")
  private LocalDateTime expiresAtOTP;

  /** Число попыток ввода. */
  @Column(name = "attempts")
  private int attempts;
}

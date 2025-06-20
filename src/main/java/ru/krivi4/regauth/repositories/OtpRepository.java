package ru.krivi4.regauth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.krivi4.regauth.models.Otp;

import java.time.LocalDateTime;
import java.util.UUID;

/**Репозиторий одноразовых Otp‑кодов*/
@Repository
public interface OtpRepository extends JpaRepository<Otp, UUID> {

  /** Удалить все записи, чей expiresAtOTP раньше указанного времени*/
  int deleteByExpiresAtOTPBefore(LocalDateTime expiresAtOTP);

  int deleteByPhoneNumber(String phoneNumber);
}

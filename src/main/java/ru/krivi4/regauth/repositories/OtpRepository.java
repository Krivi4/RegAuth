package ru.krivi4.regauth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.krivi4.regauth.models.Otp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Репозиторий одноразовых Otp‑кодов
 */
@Repository
public interface OtpRepository extends JpaRepository<Otp, UUID> {

    /**
     * Удаляет все одноразовые OTP‑коды,
     * срок действия которых истёк (expiresAtOTP раньше указанного времени).
     */
    int deleteByExpiresAtOTPBefore(LocalDateTime expiresAtOTP);

    /**
     * Удаляет все одноразовые OTP‑коды, связанные с указанным номером телефона.
     */
    int deleteByPhoneNumber(String phoneNumber);
}

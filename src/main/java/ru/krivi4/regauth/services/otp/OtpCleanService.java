package ru.krivi4.regauth.services.otp;

/**
 * Сервис для удаления устаревших OTP-кодов.
 */
public interface OtpCleanService {

    /**
     * Удаляет все OTP с истекшим сроком действия.
     */
    void cleanExpiredOTP();
}

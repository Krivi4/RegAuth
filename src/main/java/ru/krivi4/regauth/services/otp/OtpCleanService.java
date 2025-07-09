package ru.krivi4.regauth.services.otp;

/**
 * Контракт сервиса для плановой очистки устаревших OTP-кодов.
 */
public interface OtpCleanService {

    void cleanExpiredOTP();
}

package ru.krivi4.regauth.services.otp;

import java.util.UUID;

/**
 * Сервис для проверки одноразового кода (OTP).
 */
public interface OtpVerifyService {

    /**
     * Проверяет OTP-код и управляет попытками.
     */
    boolean verify(UUID otpId, String code);
}

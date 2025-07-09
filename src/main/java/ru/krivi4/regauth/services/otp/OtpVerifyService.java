package ru.krivi4.regauth.services.otp;

import java.util.UUID;

/**
 * Контракт сервиса для проверки одноразовых кодов (OTP).
 */
public interface OtpVerifyService {

    boolean verify(UUID otpId, String code);
}

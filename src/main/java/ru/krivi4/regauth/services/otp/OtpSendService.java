package ru.krivi4.regauth.services.otp;

import java.util.UUID;

/**
 * Сервис для генерации и отправки одноразового кода.
 */
public interface OtpSendService {

    /**
     * Генерирует и отправляет новый OTP на телефон.
     */
    UUID send(String phoneNumber);
}

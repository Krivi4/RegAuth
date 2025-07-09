package ru.krivi4.regauth.services.otp;

import java.util.UUID;

/**
 * Контракт сервиса генерации и отправки одноразового кода.
 */
public interface OtpSendService {

    UUID send(String phoneNumber);
}

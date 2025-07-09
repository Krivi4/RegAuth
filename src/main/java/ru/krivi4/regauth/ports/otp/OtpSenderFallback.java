package ru.krivi4.regauth.ports.otp;

import ru.krivi4.regauth.web.exceptions.SmsSendException;

/**
 * Контракт fallback-методов при ошибках отправки SMS.
 */
public interface OtpSenderFallback {
    void recover(SmsSendException ex, String phone, String text);
}

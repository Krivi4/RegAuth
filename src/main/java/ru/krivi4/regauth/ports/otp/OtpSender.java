package ru.krivi4.regauth.ports.otp;

/**
 * Отправляет SMS-сообщение (код подтверждения: одноразовый код) через API
 */
public interface OtpSender {

    /**
     * Отправляет SMS-запрос на API
     */
    void sendRequest(String phone, String text);
}

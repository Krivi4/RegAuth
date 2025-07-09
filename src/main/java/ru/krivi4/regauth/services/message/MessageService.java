package ru.krivi4.regauth.services.message;

/**
 * Контракт сервиса локализации сообщений.
 */
public interface MessageService {

    String getMessage(String code);

    String getMessage(String code, Object... args);

    String getMessage(Enum<?> a);
}

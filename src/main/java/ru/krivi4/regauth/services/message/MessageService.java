package ru.krivi4.regauth.services.message;

/**
 * Контракт сервиса локализации сообщений.
 */
public interface MessageService {

    /**
     * Возвращает локализованное сообщение по ключу.
     */
    String getMessage(String code);

    /**
     * Возвращает локализованное сообщение по ключу с параметрами.
     */
    String getMessage(String code, Object... args);

    /**
     * Возвращает локализованное сообщение для значения enum.
     */
    String getMessage(Enum<?> enumValue);
}

package ru.krivi4.regauth.services.message;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Сервис для получения локализованных сообщений из message.properties.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultMessageService implements MessageService {

    private static final Locale DEFAULT_LOCALE = new Locale("ru", "RU");
    private static final String ENUM_KEY_FORMAT = "%s.%s";
    private static final String LOG_MSG_TEMPLATE = "Ошибка получения сообщения по ключу='{}': {}";

    private final ResourceBundleMessageSource messageSource;

    /**
     * Получает локализованное сообщение по ключу.
     * Если ключ не найден — возвращает его как есть.
     */
    @Override
    public String getMessage(String code) {
        return fetchMessageSafely(code);
    }

    /**
     * Получает локализованное сообщение по ключу с подставляемыми параметрами.
     */
    @Override
    public String getMessage(String code, Object... args) {
        return fetchMessageWithArguments(code, args);
    }

    /**
     * Получает локализованное сообщение для enum.
     * Ключ формируется как "полный.путь.к.Энум.ЗНАЧЕНИЕ".
     */
    @Override
    public String getMessage(@NonNull Enum<?> enumValue) {
        String key = buildEnumKey(enumValue);
        return getMessage(key);
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Безопасно извлекает сообщение по ключу и логирует ошибку, если ключ отсутствует.
     */
    private String fetchMessageSafely(String code) {
        try {
            return messageSource.getMessage(code, null, DEFAULT_LOCALE);
        } catch (Exception e) {
            log.warn(LOG_MSG_TEMPLATE, code, e.getMessage());
            return code;
        }
    }

    /**
     * Извлекает сообщение по ключу с подстановкой аргументов.
     */
    private String fetchMessageWithArguments(String code, Object... args) {
        return messageSource.getMessage(code, args, DEFAULT_LOCALE);
    }

    /**
     * Формирует ключ для enum в формате "полный.путь.к.классу.Энум.ЗНАЧЕНИЕ".
     */
    private String buildEnumKey(Enum<?> enumValue) {
        return String.format(ENUM_KEY_FORMAT, enumValue.getClass().getName(), enumValue.name());
    }
}

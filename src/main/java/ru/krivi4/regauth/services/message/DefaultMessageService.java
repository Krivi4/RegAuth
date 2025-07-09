package ru.krivi4.regauth.services.message;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Реализация сервиса локализации сообщений.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultMessageService implements MessageService {

    private static final Locale DEFAULT_LOCALE = new Locale("ru", "RU");
    private static final String ENUM_KEY_FORMAT = "%s.%s";
    private static final String LOG_MSG_TEMPLATE   = "Ошибка получения сообщения по ключу='{}': {}";


    private final ResourceBundleMessageSource messageSource;

    /**
     * Возвращает сообщение по ключу из message.properties.
     * Если ключ не найден, возвращает сам ключ.
     */
    @Override
    public String getMessage(String code) {
        try {
            return messageSource.getMessage(code, null, DEFAULT_LOCALE);
        } catch (Exception e) {
            log.warn(LOG_MSG_TEMPLATE, code, e.getMessage());
        }
        return code;
    }

    /**
     * Возвращает сообщение по ключу с параметрами.
     */
    @Override
    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, DEFAULT_LOCALE);
    }

    /**
     * Возвращает сообщение для enum по его полному имени класса и значению.
     */
    @Override
    public String getMessage(@NonNull Enum<?> a) {
        String key = String.format(ENUM_KEY_FORMAT, a.getClass().getName(), a.name());
        return getMessage(key);
    }
}

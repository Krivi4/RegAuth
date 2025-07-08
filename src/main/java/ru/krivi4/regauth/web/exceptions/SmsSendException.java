package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**
 * Ошибка при отправке SMS через SMS.RU (HTTP 400).
 */
public class SmsSendException extends ApiException {

    /**
     * Конструктор с кодом ошибки (например, status или delivery error).
     */
    public SmsSendException(String code, MessageService messageService) {
        super(
                HttpStatus.BAD_REQUEST,
                messageService.getMessage("sms.send.status.exception", code)
        );
    }

    /**
     * Конструктор с текстом ошибки и первопричиной.
     */
    public SmsSendException(String text, Throwable cause, MessageService messageService) {
        super(
                HttpStatus.BAD_REQUEST,
                messageService.getMessage("sms.send.delivery.exception", text),
                cause
        );
    }
}

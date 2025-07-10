package ru.krivi4.regauth.web.exceptions;

import org.springframework.http.HttpStatus;
import ru.krivi4.regauth.services.message.MessageService;

/**
 * Ошибка, полученная от SMS.RU: status-/delivery-code.
 * HTTP 400 Bad Request.
 */
public class SmsSendException extends ApiException {

    private static final String MSG_STATUS_KEY = "sms.send.status.exception";
    private static final String MSG_DELIVERY_KEY = "sms.send.delivery.exception";

    public SmsSendException(String code, MessageService messageService) {
        super(HttpStatus.BAD_REQUEST, messageService.getMessage(MSG_STATUS_KEY, code));
    }

    public SmsSendException(String text, Throwable cause, MessageService messageService) {
        super(HttpStatus.BAD_REQUEST, messageService.getMessage(MSG_DELIVERY_KEY, text), cause);
    }
}

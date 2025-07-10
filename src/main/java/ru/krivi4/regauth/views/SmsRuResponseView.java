package ru.krivi4.regauth.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * Обёртка ответа от API SMS.ru при отправке SMS.
 */
@Data
public class SmsRuResponseView {

    private static final String STATUS_TEXT_KEY = "status_text";

    private String status;

    @JsonProperty(STATUS_TEXT_KEY)
    private String statusText;

    /**
     * Детальная информация по каждому отправленному SMS,
     * Ключ — номер получателя
     * Значение — статус и описание.
     */
    private Map<String, SmsInfo> sms;

    @Data
    public static class SmsInfo {
        private String status;
        @JsonProperty(STATUS_TEXT_KEY)
        private String statusText;
    }
}

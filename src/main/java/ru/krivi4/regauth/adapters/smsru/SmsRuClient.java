package ru.krivi4.regauth.adapters.smsru;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.krivi4.regauth.ports.otp.OtpSender;
import ru.krivi4.regauth.ports.otp.OtpSenderFallback;
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.views.SmsRuResponseView;
import ru.krivi4.regauth.web.exceptions.SmsSendException;

/**
 * Отправляет SMS-сообщение (код подтверждения: одноразовый код) через API SMS.ru.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsRuClient implements OtpSender, OtpSenderFallback {

    private static final int MAX_ATTEMPTS = 3;
    private static final long BACKOFF_DELAY_MS = 2000;
    private static final String VALUE_JSON_ENABLED = "1";
    private static final String VALUE_TEST_MODE = "1";
    private static final String STATUS_OK = "OK";
    private static final String STATUS_NULL_RESPONSE = "NULL_RESPONSE";
    private static final boolean BUILD_ENCODED = false;
    private static final String MESSAGE_RETRIES_EXCEPTION = "sms.send.retries.exception";
    private static final String PARAM_API_ID = "api_id";
    private static final String PARAM_TO = "to";
    private static final String PARAM_MSG = "msg";
    private static final String PARAM_FROM = "from";
    private static final String PARAM_JSON = "json";
    private static final String PARAM_TEST = "test";
    private static final String LOG_RETRIES_ERROR_TPL   =
            "SMS не отправлена после {} попыток. phone={}, text=\"{}\". Причина: {}";


    private final MessageService messageService;
    private final RestTemplate restTemplate;

    @Value("${smsru.base-url}")
    private String smsBaseUrl;
    @Value("${smsru.api-id}")
    private String apiId;
    @Value("${smsru.from}")
    private String from;

    /**
     * Отправляет SMS-запрос на API SMS.ru и проверяет статус ответа.
     * Повторяет до 3 раз с задержкой 2 сек при SmsSendException
     */
    @Override
    @Retryable(
            value = SmsSendException.class,
            maxAttempts = MAX_ATTEMPTS,
            backoff = @Backoff(delay = BACKOFF_DELAY_MS)
    )
    public void sendRequest(String phoneNumber, String textMessage) {
        String url = getUrl(phoneNumber, textMessage);
        SmsRuResponseView smsRuResponseView =
                restTemplate.getForObject(url, SmsRuResponseView.class);
        checkForResponse(smsRuResponseView);
    }

    /**
     * Метод для обработки, если все попытки ретрая не увенчались успехом.
     */
    @Override
    @Recover
    public void recover(SmsSendException smsSendException, String phoneNumber, String textMessage) {
        String message = messageService.getMessage(MESSAGE_RETRIES_EXCEPTION);

        log.error(LOG_RETRIES_ERROR_TPL,
                MAX_ATTEMPTS, phoneNumber, textMessage, smsSendException.getMessage());

        throw new SmsSendException(message, smsSendException, messageService);
    }

    //*---------------Вспомогательные методы -----------*//

    /**
     * Формирует URL для запроса к API SMS.ru
     */
    private String getUrl(String phoneNumber, String textMessage) {
        return UriComponentsBuilder
                .fromHttpUrl(smsBaseUrl)
                .queryParam(PARAM_API_ID, apiId)
                .queryParam(PARAM_TO, phoneNumber)
                .queryParam(PARAM_MSG, textMessage)
                .queryParam(PARAM_FROM, from)
                .queryParam(PARAM_JSON, VALUE_JSON_ENABLED)
                .queryParam(PARAM_TEST, VALUE_TEST_MODE) //TODO Раскомментировать - для тестов(вместо смс код выведется в логах)
                .build(BUILD_ENCODED)
                .toUriString();
    }

    /**
     * Проверяет ответ от API на ошибки
     */
    private void checkForResponse(SmsRuResponseView smsRuResponseView) {
        checkForErroneousStatus(smsRuResponseView);
        checkForUnsuccessfulDelivery(smsRuResponseView);
    }

    /**
     * Проверяет статус доставки каждого SMS
     * Если статус не OK, выбрасывает исключение
     */
    private void checkForUnsuccessfulDelivery(SmsRuResponseView smsRuResponseView) {
        for (SmsRuResponseView.SmsInfo info : smsRuResponseView.getSms().values()) {
            if (!STATUS_OK.equalsIgnoreCase(info.getStatus())) {
                throw new SmsSendException(info.toString(), messageService);
            }
        }
    }

    /**
     * Проверяет общий статус ответа API
     */
    private void checkForErroneousStatus(SmsRuResponseView smsRuResponseView) {
        String code;
        if (smsRuResponseView != null) {
            code = smsRuResponseView.getStatus();
        } else {
            code = STATUS_NULL_RESPONSE;
        }
        if (smsRuResponseView == null || !STATUS_OK.equalsIgnoreCase(code)) {
            throw new SmsSendException(code, messageService);
        }
    }

}

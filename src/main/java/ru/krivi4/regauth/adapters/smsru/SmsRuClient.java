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
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.views.SmsRuResponseView;
import ru.krivi4.regauth.web.exceptions.SmsSendException;

/**
 * Отправляет SMS-сообщение (код подтверждения: одноразовый код) через API SMS.ru.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsRuClient implements OtpSender {

    private final MessageService messageService;

    @Value("${smsru.base-url}")
    private String smsBaseUrl;

    @Value("${smsru.api-id}")
    private String apiId;

    @Value("${smsru.from}")
    private String from;

    private final RestTemplate restTemplate;

    private static final int MAX_ATTEMPTS = 3;

    /**
     * Отправляет SMS-запрос на API SMS.ru и проверяет статус ответа.
     * Повторяет до 3 раз с задержкой 2 сек при SmsSendException
     */
    @Override
    @Retryable(
            value = SmsSendException.class,
            maxAttempts = MAX_ATTEMPTS,
            backoff = @Backoff(delay = 2000)
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
    @Recover
    public void recover(SmsSendException ex, String phoneNumber, String textMessage) {
        String msg = messageService.getMessage("sms.send.retries.exception");

        log.error("SMS не отправлена после {} попыток. phone={}, text=\"{}\". Причина: {}",
                MAX_ATTEMPTS, phoneNumber, textMessage, ex.getMessage());

        throw new SmsSendException(msg, ex, messageService);
    }


    private String getUrl(String phoneNumber, String textMessage) {
        return UriComponentsBuilder
                .fromHttpUrl(smsBaseUrl)
                .queryParam("api_id", apiId)
                .queryParam("to", phoneNumber)
                .queryParam("msg", textMessage)
                .queryParam("from", from)
                .queryParam("json", 1)
                .queryParam("test", 1) //TODO Раскомментировать - для тестов(вместо смс код выведется в логах)
                .build(false)
                .toUriString();
    }

    private void checkForResponse(SmsRuResponseView smsRuResponseView) {

        checkForErroneousStatus(smsRuResponseView);
        checkForUnsuccessfulDelivery(smsRuResponseView);
    }

    private void checkForUnsuccessfulDelivery(SmsRuResponseView smsRuResponseView) {
        for (SmsRuResponseView.SmsInfo info : smsRuResponseView.getSms().values()) {
            if (!"OK".equalsIgnoreCase(info.getStatus())) {
                throw new SmsSendException(info.toString(), messageService);
            }
        }
    }

    private void checkForErroneousStatus(SmsRuResponseView smsRuResponseView) {
        String code;
        if (smsRuResponseView != null) {
            code = smsRuResponseView.getStatus();
        } else {
            code = "NULL_RESPONSE";
        }
        if (smsRuResponseView == null || !"OK".equalsIgnoreCase(code)) {
            throw new SmsSendException(code, messageService);
        }
    }

}

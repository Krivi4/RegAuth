package ru.krivi4.regauth.adapters.smsru;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.krivi4.regauth.ports.otp.OtpSender;
import ru.krivi4.regauth.config.SmsProperties;
import ru.krivi4.regauth.views.SmsRuResponse;

/**Отправляет SMS-сообщение (код подтверждения: одноразовый код) через API SMS.ru.*/
@Service
@RequiredArgsConstructor
public class SmsRuSender implements OtpSender {

  private final RestTemplate restTemplate;
  private final SmsProperties smsProperties;

  /**Отправляет SMS-запрос на API SMS.ru и проверяет статус ответа.*/
  @Override
  public void sendRequest(String phoneNumber, String textMessage) {

    String url = UriComponentsBuilder
        .fromHttpUrl("https://sms.ru/sms/send")
        .queryParam("api_id", smsProperties.getApiId())
        .queryParam("to", phoneNumber)
        .queryParam("msg", textMessage)
        .queryParam("from", smsProperties.getFrom())
        .queryParam("json",1)
       // .queryParam("test" ,1) //TODO Раскомментировать - для тестов(вместо смс выведется в логах)
        .build(false)
        .toUriString();

  SmsRuResponse smsRuResponse =
      restTemplate.getForObject(url, SmsRuResponse.class);
  if (smsRuResponse == null
      || !"OK".equalsIgnoreCase(smsRuResponse.getStatus())) {
    throw new IllegalStateException("SMS.RU ошибка: " + smsRuResponse);
  }

  for (SmsRuResponse.SmsInfo info : smsRuResponse.getSms().values()) {
    if (!"OK".equalsIgnoreCase(info.getStatus())) {
      throw new IllegalStateException(
          "SMS.RU не удалось отправить сообщение: " + info);
      }
    }
  }
}

package ru.krivi4.regauth.adapters.smsru;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.krivi4.regauth.ports.otp.OtpSender;
import ru.krivi4.regauth.config.SmsProperties;
import ru.krivi4.regauth.views.SmsRuResponse;

/**Отправляет SMS-сообщение (код подтверждения: одноразовый код) через API SMS.ru.*/
@Service
@RequiredArgsConstructor
public class SmsRuClient implements OtpSender {

  @Value("${smsru.base-url}")
  private String smsBaseUrl;

  @Value("${smsru.api-id}")
  private String apiId;

  @Value("${smsru.from}")
  private String from;

  private final RestTemplate restTemplate;

  /**Отправляет SMS-запрос на API SMS.ru и проверяет статус ответа.*/
  @Override
  public void sendRequest(String phoneNumber, String textMessage) {

    String url = UriComponentsBuilder
      .fromHttpUrl(smsBaseUrl)
      .queryParam("api_id", apiId)
      .queryParam("to", phoneNumber)
      .queryParam("msg", textMessage)
      .queryParam("from", from)
      .queryParam("json",1)
//      .queryParam("test" ,1) //TODO Раскомментировать - для тестов(вместо смс выведется в логах)
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

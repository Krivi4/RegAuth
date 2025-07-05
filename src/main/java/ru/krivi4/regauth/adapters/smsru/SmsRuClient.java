package ru.krivi4.regauth.adapters.smsru;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.krivi4.regauth.ports.otp.OtpSender;
import ru.krivi4.regauth.views.SmsRuResponseView;

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
      .queryParam("test" ,1) //TODO Раскомментировать - для тестов(вместо смс выведется в логах)
      .build(false)
      .toUriString();

  SmsRuResponseView smsRuResponseView =
    restTemplate.getForObject(url, SmsRuResponseView.class);
  if (smsRuResponseView == null
      || !"OK".equalsIgnoreCase(smsRuResponseView.getStatus())) {
    throw new IllegalStateException("SMS.RU ошибка: " + smsRuResponseView);
  }

  for (SmsRuResponseView.SmsInfo info : smsRuResponseView.getSms().values()) {
    if (!"OK".equalsIgnoreCase(info.getStatus())) {
      throw new IllegalStateException(
        "SMS.RU не удалось отправить сообщение: " + info);
      }
    }
  }
}

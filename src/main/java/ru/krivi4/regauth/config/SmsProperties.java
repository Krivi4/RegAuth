package ru.krivi4.regauth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**Свойства SMS.ru, загружаемые из application.properties (prefix = smsru).*/
@Configuration
@ConfigurationProperties(prefix = "smsru")

@Getter
@Setter
public class SmsProperties {
  /** API-ключ SMS.ru. */
  private String apiId;
  /** Имя отправителя. */
  private String from;
  /** Время жизни Otp (минуты). */
  private int ttlMinutes;
  /** Максимальное число попыток ввода Otp. */
  private int attempts;
}

package ru.krivi4.regauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**Общая конфигурация бинов приложения.*/
@Configuration
public class AppConfig {

  @Bean
  public RestTemplate restTemplate() {

    return new RestTemplate();
  }
}
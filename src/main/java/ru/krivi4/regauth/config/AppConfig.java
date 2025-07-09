package ru.krivi4.regauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

/**
 * Общая конфигурация бинов приложения.
 */
@Configuration
@EnableRetry
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {

        return new RestTemplate();
    }
}
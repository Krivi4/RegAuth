package ru.krivi4.regauth.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.krivi4.regauth.jwt.Phase;
import ru.krivi4.regauth.jwt.handler.JwtPhaseHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**Конфигурационный класс для инициализации маппинга фаз JWT на соответствующие обработчики.*/
@Configuration
public class JwtPhaseConfig {

  /**Формирует отображение фаз JWT на их обработчики.*/
  @Bean
  public Map<Phase, JwtPhaseHandler> phaseHandlerMap(List<JwtPhaseHandler> handlers) {
    return handlers.stream()
      .collect(Collectors.toMap(JwtPhaseHandler::phase, h -> h));
  }
}
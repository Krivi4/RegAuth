package ru.krivi4.regauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RegAuthApplication {

  /**
   * Точка запуска
   */
  public static void main(String[] args) {
    SpringApplication.run(RegAuthApplication.class, args);
  }
}


package ru.krivi4.regauth.views;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**Обёртка ошибки REST.*/
@Getter
@AllArgsConstructor
public class ErrorResponseView {

  private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
  private final LocalDateTime timestamp;
  private final int status;
  private final String error;
  private final String message;
  private final String path;
}
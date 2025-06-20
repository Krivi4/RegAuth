package ru.krivi4.regauth.web.aspect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.krivi4.regauth.views.ErrorResponse;
import ru.krivi4.regauth.web.exceptions.ApiException;
import ru.krivi4.regauth.web.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 * Перехватывает специфичные и общие ошибки
 * Формирует ErrorResponse
 * Возвращает корректный HTTP-статус.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**Обрабатывает все исключения типа ApiException.*/
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResponse> handleApi(ApiException apiException, WebRequest webRequest) {

    ErrorResponse body = new ErrorResponse(
      LocalDateTime.now(),
      apiException.getStatus().value(),
      apiException.getStatus().getReasonPhrase(),
      apiException.getMessage(),
      webRequest.getDescription(false).replace("uri=", "")
    );

    if (apiException instanceof ValidationException) {
      ValidationException validationException = (ValidationException) apiException;
      log.debug("Ошибка валидации: {}", validationException   .getErrors());
    }

    return ResponseEntity.status(apiException.getStatus()).body(body);
  }

  /**Ловит ошибки валидации Bean-DTO и преобразует их в ApiException.*/
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleDtoValidation(
    MethodArgumentNotValidException ex) {

    List<FieldError> errors = ex.getBindingResult().getFieldErrors();

    Map<String, String> fieldErrors = new HashMap<>();
    for (FieldError error : errors) {
      String field = error.getField();
      if (!fieldErrors.containsKey(field)) {
        fieldErrors.put(field, error.getDefaultMessage());
      }
    }
    throw new ValidationException(fieldErrors);
  }

  /**Обрабатывает остальные неожиданные исключения, возвращая HTTP 500.*/
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleOthers(Exception ex, WebRequest req) {
    log.error("Unexpected error", ex);

    ErrorResponse body = new ErrorResponse(
      LocalDateTime.now(),
      500,
      "Internal Server Error",
      "Внутренняя ошибка сервера",
      req.getDescription(false).replace("uri=", "")
    );
    return ResponseEntity.status(500).body(body);
  }
}

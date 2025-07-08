package ru.krivi4.regauth.web.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.views.ErrorResponseView;
import ru.krivi4.regauth.web.exceptions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Глобальный обработчик исключений для REST API.
 * Каждое кастомное исключение имеет свой метод.
 * Формирует стандартный ответ ErrorResponseView и возвращает HTTP-статус.
 */
@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageService messageService;

    private static final String URI_PREFIX = "uri=";

    /**
     * Обрабатывает LoginBadCredentialsException (HTTP 401).
     */
    @ExceptionHandler(LoginBadCredentialsException.class)
    public ResponseEntity<ErrorResponseView> handleLoginBadCredentialsException(
            LoginBadCredentialsException loginBadCredentialsException,
            WebRequest webRequest) {

        String exceptionMessage = loginBadCredentialsException.getMessage();

        logException("Ошибка авторизации: {}", exceptionMessage, log::warn);

        return buildErrorResponseEntity(
                loginBadCredentialsException.getStatus(),exceptionMessage, webRequest
        );

    }

    /**
     * Обрабатывает OtpTokenInvalidException (HTTP 401).
     */
    @ExceptionHandler(OtpTokenInvalidException.class)
    public ResponseEntity<ErrorResponseView> handleOtpTokenInvalidException(
            OtpTokenInvalidException otpTokenInvalidException,
            WebRequest webRequest) {

        String exceptionMessage = otpTokenInvalidException.getMessage();

        logException("Неверный OTP: {}", exceptionMessage, log::warn);

        return buildErrorResponseEntity(
                otpTokenInvalidException.getStatus(),exceptionMessage, webRequest
        );
    }

    /**
     * Обрабатывает PersonUsernameNotFoundException (HTTP 404).
     */
    @ExceptionHandler(PersonUsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseView> handlePersonUsernameNotFoundException(
            PersonUsernameNotFoundException personUsernameNotFoundException,
            WebRequest webRequest) {

        String exceptionMessage = personUsernameNotFoundException.getMessage();

        logException("Пользователь не найден: {}", exceptionMessage, log::info);

        return buildErrorResponseEntity(
                personUsernameNotFoundException.getStatus(),
                exceptionMessage,
                webRequest
        );
    }

    /**
     * Обрабатывает PersonPhoneNotFoundException (HTTP 404).
     */
    @ExceptionHandler(PersonPhoneNotFoundException.class)
    public ResponseEntity<ErrorResponseView> handlePersonPhoneNotFoundException(
            PersonPhoneNotFoundException personPhoneNotFoundException,
            WebRequest webRequest) {

        String exceptionMessage = personPhoneNotFoundException.getMessage();

        logException("Телефон не найден: {}", exceptionMessage, log::info);

        return buildErrorResponseEntity(personPhoneNotFoundException.getStatus(), exceptionMessage, webRequest);
    }

    /**
     * Обрабатывает RefreshTokenInvalidException (HTTP 401).
     */
    @ExceptionHandler(RefreshTokenInvalidException.class)
    public ResponseEntity<ErrorResponseView> handleRefreshTokenInvalidException(
            RefreshTokenInvalidException refreshTokenInvalidException,
            WebRequest webRequest) {

        String exceptionMessage = refreshTokenInvalidException.getMessage();

        logException("Неверный Refresh-токен: {}", exceptionMessage,log::warn);

        return buildErrorResponseEntity(refreshTokenInvalidException.getStatus(), exceptionMessage, webRequest);
    }

    /**
     * Обрабатывает RefreshTokenNotFoundException (HTTP 404).
     */
    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<ErrorResponseView> handleRefreshTokenNotFoundException(
            RefreshTokenNotFoundException refreshTokenNotFoundException,
            WebRequest webRequest) {

        String exceptionMessage = refreshTokenNotFoundException.getMessage();

        logException("Refresh-токен не найден: {}", exceptionMessage, log::warn);

        return buildErrorResponseEntity(refreshTokenNotFoundException.getStatus(), exceptionMessage, webRequest);
    }

    /**
     * Обрабатывает DefaultRoleNotFoundException (HTTP 404).
     */
    @ExceptionHandler(DefaultRoleNotFoundException.class)
    public ResponseEntity<ErrorResponseView> handleDefaultRoleNotFoundException(
            DefaultRoleNotFoundException defaultRoleNotFoundException,
            WebRequest webRequest) {

        String exceptionMessage = defaultRoleNotFoundException.getMessage();

        logException("Базовая роль не найдена: {}", exceptionMessage, log::error);

        return buildErrorResponseEntity(defaultRoleNotFoundException.getStatus(), exceptionMessage, webRequest);
    }

    /**
     * Обрабатывает TokenRevokedException (HTTP 401).
     */
    @ExceptionHandler(TokenRevokedException.class)
    public ResponseEntity<ErrorResponseView> handleTokenRevokedException(
            TokenRevokedException tokenRevokedException,
            WebRequest webRequest) {

        String exceptionMessage = tokenRevokedException.getMessage();

        logException("Токен отозван: {}", exceptionMessage, log::warn);

        return buildErrorResponseEntity(tokenRevokedException.getStatus(), exceptionMessage, webRequest);
    }

    /**
     * Обрабатывает TypeTokenInvalidException (HTTP 400).
     */
    @ExceptionHandler(TypeTokenInvalidException.class)
    public ResponseEntity<ErrorResponseView> handleTypeTokenInvalidException(
            TypeTokenInvalidException typeTokenInvalidException,
            WebRequest webRequest) {

        String exceptionMessage = typeTokenInvalidException.getMessage();

        logException("Неверный тип токена: {}", exceptionMessage, log::warn);

        return buildErrorResponseEntity(typeTokenInvalidException.getStatus(), exceptionMessage, webRequest);
    }

    /**
     * Обрабатывает ValidationException (HTTP 400).
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseView> handleValidationException(
            ValidationException validationException,
            WebRequest webRequest) {

        String exceptionMessage = validationException.getMessage();

        logException("Ошибка валидации данных: {}", exceptionMessage, log::debug);

        return buildErrorResponseEntity(validationException.getStatus(), exceptionMessage, webRequest);
    }

    /**
     * Обрабатывает SmsSendException (HTTP 400).
     */
    @ExceptionHandler(SmsSendException.class)
    public ResponseEntity<ErrorResponseView> handleSmsSendException(
            SmsSendException smsSendException,
            WebRequest webRequest) {

        String exceptionMessage = smsSendException.getMessage();

        logException("Ошибка при отправке SMS: {}", exceptionMessage, log::error);

        return buildErrorResponseEntity(smsSendException.getStatus(), exceptionMessage, webRequest);
    }

    /**
     * Обрабатывает исключение AuthenticateSkipException(HTTP 401).
     */
    @ExceptionHandler(AuthenticateSkipException.class)
    public ResponseEntity<ErrorResponseView> handleAuthenticateSkipException(
            AuthenticateSkipException authenticateSkipException,
            WebRequest webRequest) {

        String exceptionMessage = authenticateSkipException.getMessage();

        logException("Аутентификация пропущена: {}", exceptionMessage, log::info);

        return buildErrorResponseEntity(authenticateSkipException.getStatus(),exceptionMessage, webRequest);
    }

    /**
     * Обрабатывает исключение DuplicatePersonException(HTTP 409).
     */
    @ExceptionHandler(DuplicatePersonException.class)
    public ResponseEntity<ErrorResponseView> handleDuplicatePersonException(
            DuplicatePersonException duplicatePersonException,
            WebRequest webRequest) {

        String exceptionMessage = duplicatePersonException.getMessage();

        logException("Попытка зарегистрировать уже существующего пользователя: {}", exceptionMessage, log::warn);

        return buildErrorResponseEntity(duplicatePersonException.getStatus(), exceptionMessage, webRequest);
    }

    /**
     * Обрабатывает исключение JwtInvalidException(HTTP 400).
     */
    @ExceptionHandler(JwtInvalidException.class)
    public ResponseEntity<ErrorResponseView> handleJwtInvalidException(
            JwtInvalidException jwtInvalidException,
            WebRequest webRequest) {

        String exceptionMessage = jwtInvalidException.getMessage();

        logException("Неверный или просроченный JWT: {}", exceptionMessage, log::warn);

        return buildErrorResponseEntity(jwtInvalidException.getStatus(), exceptionMessage, webRequest);
    }

    /**
     * Обрабатывает исключение PhaseUnknownException(HTTP 400).
     */
    @ExceptionHandler(PhaseUnknownException.class)
    public ResponseEntity<ErrorResponseView> handlePhaseUnknownException(
            PhaseUnknownException phaseUnknownException,
            WebRequest webRequest) {

        String exceptionMessage = phaseUnknownException.getMessage();

        logException("Неизвестная фаза JWT: {}", exceptionMessage, log::warn);

        return buildErrorResponseEntity(phaseUnknownException.getStatus(), exceptionMessage, webRequest);
    }


    /**
     * Обрабатывает MethodArgumentNotValidException:
     * конвертирует ошибки DTO в ValidationException.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        Map<String, String> fieldErrors = extractFieldErrors(methodArgumentNotValidException.getBindingResult().getFieldErrors());
        throw new ValidationException(fieldErrors, messageService);
    }

    /**
     * Обрабатывает все необработанные исключения (HTTP 500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseView> handleUnhandledException(
            Exception exception,
            WebRequest webRequest) {

        String exceptionMessage = exception.getMessage();

        log.error("Необработанное исключение: {}", exceptionMessage, exception);

        return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, exceptionMessage, webRequest);
    }

    /* ----------------- ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ----------------- */

    /**
     * Создаёт объект ErrorResponseView для возврата клиенту.
     */
    private ErrorResponseView buildErrorResponse(HttpStatus httpStatus, String message, String path) {
        return new ErrorResponseView(
                LocalDateTime.now(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                path
        );
    }

    /**
     * Извлекает путь запроса из объекта WebRequest.
     */
    private String extractRequestPath(WebRequest webRequest) {
        return webRequest.getDescription(false).replace(URI_PREFIX, "");
    }

    /**
     * Преобразует список ошибок полей в Map с именем поля и сообщением ошибки.
     */
    private Map<String, String> extractFieldErrors(List<FieldError> fieldErrorList) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : fieldErrorList) {
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            fieldErrors.putIfAbsent(fieldName, errorMessage);
        }
        return fieldErrors;
    }

    /**
     * Формирует ResponseEntity<ErrorResponseView> с данным статусом и текстом ошибки.
     */
    private ResponseEntity<ErrorResponseView> buildErrorResponseEntity(
            HttpStatus status,
            String message,
            WebRequest webRequest
    ) {
        return ResponseEntity
                .status(status)
                .body(buildErrorResponse(
                        status,
                        message,
                        extractRequestPath(webRequest)
                ));
    }

    /** Логирует сообщение с нужным уровнем и префиксом. */
    private void logException(String prefix, String message, Consumer<String> logMethod) {
        logMethod.accept(prefix + message);
    }


}

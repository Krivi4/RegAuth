package ru.krivi4.regauth.views;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Простейший ответ админ-эндпоинтов:
 * содержит только человекочитаемое сообщение.
 */
@Getter
@AllArgsConstructor
public class AdminActionResponseView {
    private final String message;
}
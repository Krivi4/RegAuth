package ru.krivi4.regauth.jwt.phase;

/**
 * Контракт для парсинга строки в перечисление Phase.
 * Определяет метод преобразования текстового значения из JWT claim
 * в одну из поддерживаемых фаз аутентификации.
 */
public interface PhaseParser {

    /**
     * Преобразует строковое значение из JWT claim в перечисление Phase.
     */
    Phase parse(String rawPhase);
}

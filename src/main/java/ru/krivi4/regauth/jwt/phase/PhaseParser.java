package ru.krivi4.regauth.jwt.phase;

/**
 * Контракт для парсинга строки в enum Phase.
 */
public interface PhaseParser {
    Phase parse(String rawPhase);
}

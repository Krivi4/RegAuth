package ru.krivi4.regauth.jwt.phase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.web.exceptions.PhaseUnknownException;

/**
 * Парсер фазы JWT: преобразует строку claim "phase" в enum Phase.
 */
@Component
@RequiredArgsConstructor
public class JwtPhaseParser implements PhaseParser {

    private static final String PHASE_MISSING_KEY = "phase.claim.missing.exception";

    private final MessageService messageService;

    /**
     * Парсит строку с фазой из JWT и возвращает enum Phase.
     */
    @Override
    public Phase parse(String rawPhase) {
        ensurePhasePresent(rawPhase);
        return toPhaseEnum(rawPhase);
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Проверяет, что строка фазы не пустая.
     */
    private void ensurePhasePresent(String rawPhase) {
        if (rawPhase == null || rawPhase.isBlank()) {
            String message = messageService.getMessage(PHASE_MISSING_KEY);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Преобразует строку в enum Phase или выбрасывает PhaseUnknownException.
     */
    private Phase toPhaseEnum(String rawPhase) {
        try {
            return Phase.valueOf(rawPhase);
        } catch (IllegalArgumentException ex) {
            throw new PhaseUnknownException(rawPhase, messageService);
        }
    }
}

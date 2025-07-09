package ru.krivi4.regauth.jwt.phase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.services.message.DefaultMessageService;
import ru.krivi4.regauth.web.exceptions.PhaseUnknownException;

@Component
@RequiredArgsConstructor
public class JwtPhaseParser implements PhaseParser {

    private static final String MESSAGE_PHASE_MISSING = "phase.claim.missing.exception";

    private final DefaultMessageService defaultMessageService;

    /**
     * Парсит claim `phase` из JWT в enum Phase
     */
    @Override
    public Phase parse(String rawPhase) {
        if (rawPhase == null || rawPhase.isEmpty()) {
            throw new IllegalArgumentException(
                    defaultMessageService.getMessage(MESSAGE_PHASE_MISSING));
        }
        try {
            return Phase.valueOf(rawPhase);
        } catch (IllegalArgumentException e) {
            throw new PhaseUnknownException(
                    rawPhase, defaultMessageService
            );
        }
    }
}

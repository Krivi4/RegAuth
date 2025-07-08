package ru.krivi4.regauth.jwt.phase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.web.exceptions.PhaseUnknownException;

@Component
@RequiredArgsConstructor
public class PhaseParser {

    private final MessageService messageService;

    /**
     * Парсит claim `phase` из JWT в enum Phase
     */

    public Phase parse(String rawPhase) {
        if (rawPhase == null || rawPhase.isEmpty()) {
            throw new IllegalArgumentException(
                    messageService.getMessage("phase.claim.missing.exception"));
        }
        try {
            return Phase.valueOf(rawPhase);
        } catch (IllegalArgumentException e) {
            throw new PhaseUnknownException(
                   rawPhase, messageService
            );
        }
    }
}

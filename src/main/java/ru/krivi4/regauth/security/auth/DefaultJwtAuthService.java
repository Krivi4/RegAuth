package ru.krivi4.regauth.security.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.krivi4.regauth.jwt.phase.Phase;
import ru.krivi4.regauth.jwt.handler.JwtPhaseHandler;
import ru.krivi4.regauth.jwt.phase.PhaseParser;
import ru.krivi4.regauth.jwt.util.JwtUtil;
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.web.exceptions.JwtInvalidException;
import ru.krivi4.regauth.web.exceptions.PhaseUnknownException;

import java.util.Map;

/**
 * Сервис аутентификации JWT‑токенов.
 * Проверяет токен, извлекает фазу и делегирует обработчику фазы.
 */
@Service
@RequiredArgsConstructor
public class DefaultJwtAuthService implements JwtAuthService {

    private static final String CLAIM_PHASE = "phase";

    private final JwtUtil jwtUtil;
    private final PhaseParser phaseParser;
    private final Map<Phase, JwtPhaseHandler> handlers;
    private final MessageService messageService;

    /**
     * Проверяет JWT‑токен и возвращает Authentication.
     */
    @Override
    public Authentication authenticate(String rawJwt) {
        DecodedJWT decodedJWT = decodeToken(rawJwt);
        Phase phase = extractPhase(decodedJWT);
        JwtPhaseHandler handler = getHandler(phase);
        return handleWith(handler, decodedJWT);
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Декодирует JWT и проверяет подпись.
     */
    private DecodedJWT decodeToken(String rawJwt) {
        try {
            return jwtUtil.decode(rawJwt);
        } catch (JWTVerificationException e) {
            throw new JwtInvalidException(messageService);
        }
    }

    /**
     * Извлекает фазу из claim и парсит в enum Phase.
     */
    private Phase extractPhase(DecodedJWT decodedJWT) {
        String rawPhase = decodedJWT.getClaim(CLAIM_PHASE).asString();
        return phaseParser.parse(rawPhase);
    }

    /**
     * Получает обработчик фазы из карты обработчиков.
     */
    private JwtPhaseHandler getHandler(Phase phase) {
        JwtPhaseHandler handler = handlers.get(phase);
        if (handler == null) {
            throw new PhaseUnknownException(phase.name(), messageService);
        }
        return handler;
    }

    /**
     * Делегирует обработчику формирование Authentication.
     */
    private Authentication handleWith(JwtPhaseHandler handler, DecodedJWT jwt) {
        return handler.handle(jwt);
    }
}

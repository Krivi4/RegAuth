package ru.krivi4.regauth.security.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.krivi4.regauth.jwt.phase.Phase;
import ru.krivi4.regauth.jwt.handler.JwtPhaseHandler;
import ru.krivi4.regauth.jwt.phase.PhaseParser;
import ru.krivi4.regauth.jwt.util.DefaultJwtUtil;
import ru.krivi4.regauth.services.message.DefaultMessageService;
import ru.krivi4.regauth.web.exceptions.JwtInvalidException;
import ru.krivi4.regauth.web.exceptions.PhaseUnknownException;

import java.util.Map;

/**
 * Реализация сервиса аутентификации JWT‑токенов.
 * Проверяет валидность токена, извлекает фазу и передаёт обработчику для формирования Authentication.
 */
@Service
@RequiredArgsConstructor
public class DefaultJwtAuthService implements JwtAuthService {

    private static final String CLAIM_PHASE = "phase";

    private final DefaultJwtUtil defaultJwtUtil;
    private final PhaseParser phaseParser;
    private final Map<Phase, JwtPhaseHandler> handlers;
    private final DefaultMessageService defaultMessageService;

    /**
     * Проверяет валидность JWT‑токена и возвращает объект Authentication.
     */
    @Override
    public Authentication authenticate(String rawJwt) {
        DecodedJWT decodedJWT = decodeToken(rawJwt);
        Phase phase = parsePhase(decodedJWT);
        JwtPhaseHandler handler = resolveHandler(phase);
        return handleWith(handler, decodedJWT);
    }

    // *------------Вспомогательные методы--------------*//

    /**
     * Извлекает обработчик фазы из мапы.
     */
    private JwtPhaseHandler resolveHandler(Phase phase) {
        JwtPhaseHandler handler = handlers.get(phase);
        if (handler == null) {
            throw new PhaseUnknownException(phase.name(), defaultMessageService);
        }
        return handler;
    }

    /**
     * Извлекает фазу токена из claim и парсит её в enum Phase.
     */
    private Phase parsePhase(DecodedJWT decodedJWT) {
        String rawPhase = decodedJWT.getClaim(CLAIM_PHASE).asString();
        return phaseParser.parse(rawPhase);
    }

    /**
     * Декодирует и проверяет подпись JWT‑токена.
     */
    private DecodedJWT decodeToken(String rawJwt) {
        try {
            return defaultJwtUtil.decode(rawJwt);
        } catch (JWTVerificationException e) {
            throw new JwtInvalidException(defaultMessageService);
        }
    }

    /**
     * Передаёт декодированный токен обработчику фазы для формирования Authentication.
     */
    private Authentication handleWith(JwtPhaseHandler handler, DecodedJWT jwt) {
        return handler.handle(jwt);
    }
}

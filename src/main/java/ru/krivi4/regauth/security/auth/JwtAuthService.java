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
 * Сервис аутентификации JWT-токенов.
 */
@Service
@RequiredArgsConstructor
public class JwtAuthService {

    private final JwtUtil jwtUtil;
    private final PhaseParser phaseParser;
    private final Map<Phase, JwtPhaseHandler> handlers;
    private final MessageService messageService;

    /**
     * Проверяет валидность JWT и возвращает готовый объект Authentication
     */
    public Authentication authenticate(String rawJwt) {
        DecodedJWT decodedJWT = decodeToken(rawJwt);
        Phase phase = phaseParse(decodedJWT);
        JwtPhaseHandler handler = resolveHandler(phase);
        return handleWith(handler, decodedJWT);
    }


    private JwtPhaseHandler resolveHandler(Phase phase) {
        JwtPhaseHandler handler = handlers.get(phase);
        if (handler == null) {
            throw new PhaseUnknownException(phase.name(), messageService);
        }
        return handler;
    }

    private Phase phaseParse(DecodedJWT decodedJWT) {
        String rawPhase = decodedJWT.getClaim("phase").asString();
        return phaseParser.parse(rawPhase);
    }

    private DecodedJWT decodeToken(String rawJwt) {
        try {
            return jwtUtil.decode(rawJwt);
        } catch (JWTVerificationException e) {
            throw new JwtInvalidException(messageService);
        }
    }

    private Authentication handleWith(JwtPhaseHandler handler, DecodedJWT jwt) {
        return handler.handle(jwt);
    }
}
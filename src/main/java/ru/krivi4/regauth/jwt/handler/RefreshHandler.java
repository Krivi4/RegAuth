package ru.krivi4.regauth.jwt.handler;


import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.jwt.phase.Phase;
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.web.exceptions.AuthenticateSkipException;

/**
 * Обработчик фазы REFRESH: пропускает аутентификацию для refresh-токена.
 */
@Component
@RequiredArgsConstructor
public class RefreshHandler implements JwtPhaseHandler {

    private final MessageService messageService;

    /**
     * Возвращает фазу REFRESH
     */
    @Override
    public Phase phase() {
        return Phase.REFRESH;
    }

    /**
     * Пропускаем аутентификацию и продолжаем обработку запроса
     */
    @Override
    public Authentication handle(DecodedJWT jwt) {
        throw new AuthenticateSkipException(messageService);
    }
}
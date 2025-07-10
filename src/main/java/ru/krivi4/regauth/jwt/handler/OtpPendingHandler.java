package ru.krivi4.regauth.jwt.handler;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.jwt.phase.Phase;
import ru.krivi4.regauth.services.message.MessageService;
import ru.krivi4.regauth.web.exceptions.AuthenticateSkipException;

/**
 * Обработчик фазы OTP_PENDING.
 * Пропускает аутентификацию до подтверждения OTP‑кода.
 */
@Component
@RequiredArgsConstructor
public class OtpPendingHandler implements JwtPhaseHandler {

    private final MessageService messageService;

    /**
     * Возвращает фазу OTP_PENDING.
     */
    @Override
    public Phase phase() {
        return Phase.OTP_PENDING;
    }

    /**
     * Прерывает обработку для дальнейшего ввода OTP‑кода.
     */
    @Override
    public Authentication handle(DecodedJWT jwt) {
        throw new AuthenticateSkipException(messageService);
    }
}

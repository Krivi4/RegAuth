package ru.krivi4.regauth.jwt.handler;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.jwt.phase.Phase;
import ru.krivi4.regauth.services.message.DefaultMessageService;
import ru.krivi4.regauth.web.exceptions.AuthenticateSkipException;

/**
 * Обработчик фазы OTP_PENDING: пропускает аутентификацию до ввода Otp.
 */
@Component
@RequiredArgsConstructor
public class OtpPendingHandler implements JwtPhaseHandler {

    private final DefaultMessageService defaultMessageService;

    /**
     * Возвращает фазу Otp-PENDING.
     */
    @Override
    public Phase phase() {
        return Phase.OTP_PENDING;
    }

    /**
     * Пропускаем аутентификацию и продолжаем обработку запроса
     */
    @Override
    public Authentication handle(DecodedJWT jwt) {
        throw new AuthenticateSkipException(defaultMessageService);
    }
}
package ru.krivi4.regauth.jwt.handler;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.jwt.phase.Phase;
import ru.krivi4.regauth.services.auth.PersonDetailsService;
import ru.krivi4.regauth.services.message.DefaultMessageService;
import ru.krivi4.regauth.services.tokens.DefaultAccessTokenBlackListService;
import ru.krivi4.regauth.web.exceptions.TokenRevokedException;

import java.util.UUID;

/**
 * Обработчик фазы FULL: проверяет черный список и формирует Authentication.
 */
@Component
@RequiredArgsConstructor
public class FullHandler implements JwtPhaseHandler {

    private static final String CLAIM_USERNAME = "username";

    private final PersonDetailsService users;
    private final DefaultAccessTokenBlackListService blacklist;
    private final DefaultMessageService defaultMessageService;

    /**
     * Возвращает фазу FULL.
     */
    @Override
    public Phase phase() {
        return Phase.FULL;
    }

    /**
     * Проверяет токен на блокировку и возвращает Authentication.
     */
    @Override
    public Authentication handle(DecodedJWT jwt) {
        UUID jti = UUID.fromString(jwt.getId());
        String username = jwt.getClaim(CLAIM_USERNAME).asString();

        checkRevoked(jti);
        return buildAuthToken(loadUser(username));
    }

    //*----------Вспомогательные методы----------*//

    /**
     * Создает объект Authentication для Spring Security.
     */
    private Authentication buildAuthToken(UserDetails user) {
        return new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), user.getAuthorities());
    }

    /**
     * Проверяет, отозван ли токен по идентификатору jti.
     */
    private void checkRevoked(UUID jti) {
        if (blacklist.isBlocked(jti)) {
            throw new TokenRevokedException(defaultMessageService);
        }
    }

    /**
     * Загружает пользователя по имени.
     */
    private UserDetails loadUser(String username) {
        return users.loadUserByUsername(username);
    }
}
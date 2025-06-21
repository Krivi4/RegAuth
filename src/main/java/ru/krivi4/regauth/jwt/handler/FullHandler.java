    package ru.krivi4.regauth.jwt.handler;

    import com.auth0.jwt.interfaces.DecodedJWT;
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Component;
    import ru.krivi4.regauth.jwt.Phase;
    import ru.krivi4.regauth.services.auth.PersonDetailsService;
    import ru.krivi4.regauth.services.tokens.AccessTokenBlackListService;
    import ru.krivi4.regauth.web.exceptions.TokenRevokedException;

    import java.util.UUID;

/**Обработчик фазы FULL: проверяет черный список и формирует Authentication.*/
@Component
@RequiredArgsConstructor
public class FullHandler implements JwtPhaseHandler {

  private final PersonDetailsService users;
  private final AccessTokenBlackListService blacklist;

  /** Возвращает фазу FULL. */
  @Override public Phase phase() {
    return Phase.FULL;
  }

  /** Проверяет токен на блокировку и возвращает Authentication. */
  @Override
  public Authentication handle(DecodedJWT jwt) {

    UUID jti = UUID.fromString(jwt.getId());
    if (blacklist.isBlocked(jti)){
      throw new TokenRevokedException();
    }

    String username = jwt.getClaim("username").asString();
    UserDetails user = users.loadUserByUsername(username);
    return new UsernamePasswordAuthenticationToken(
      user, user.getPassword(), user.getAuthorities());
  }
}
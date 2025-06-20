package ru.krivi4.regauth.security.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.krivi4.regauth.security.auth.JwtAuthService;
import ru.krivi4.regauth.web.exceptions.ApiException;
import ru.krivi4.regauth.web.exceptions.AuthenticateSkipException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**Фильтр запросов для проверки JWT(один раз за запрос)*/
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtAuthService jwtAuthService;

  /**
   * Перехватывает HTTP-запрос, извлекает JWT из заголовка Authorization,
   * проверяет его валидность через JwtAuthService и при успешной проверке
   * устанавливает Authentication в SecurityContext для последующей обработки.
   */
  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String jwt = authHeader.substring(7).trim();

    if (jwt.isBlank()) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
        "Недопустимый токен JWT в заголовке на предъявителя");
      return;
    }

    try {
      Authentication authentication = jwtAuthService.authenticate(jwt);

      if (SecurityContextHolder.getContext().getAuthentication() == null) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }

      filterChain.doFilter(request, response);

    } catch (AuthenticateSkipException authenticateSkipException) {
      filterChain.doFilter(request, response);
    } catch (ApiException apiException) {
      response.sendError(apiException.getStatus().value(), apiException.getMessage());
    }
  }
}

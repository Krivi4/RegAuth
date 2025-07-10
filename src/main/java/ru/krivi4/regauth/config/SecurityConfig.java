package ru.krivi4.regauth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.krivi4.regauth.security.filter.JwtFilter;
import ru.krivi4.regauth.security.filter.JwtLogoutSuccessHandler;

/**
 * Конфигурация Spring Security:
 * настройка JWT-фильтра, правил доступа и stateless-сессий.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String LOGOUT_URL = "/api/v1/auth/logout";
    private static final String[] ALLOWED_URLS = {
            "/api/v1/auth/login",
            "/api/v1/auth/registration",
            "/api/v1/auth/registration/verify",
            "/api/v1/auth/login/verify",
            "/api/v1/auth/refresh",
            "/error",
    };
    private static final String[] SWAGGER_URLS = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    };

    private final JwtFilter jwtFilter;
    private final JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

    /**
     * Настраивает HTTP Security:
     * - отключение CSRF,
     * - настройка правил доступа,
     * - конфигурация logout и stateless-сессий,
     * - добавление JWT-фильтра.
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(SWAGGER_URLS).hasRole(ROLE_ADMIN)
                .antMatchers(ALLOWED_URLS).permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutUrl(LOGOUT_URL)
                .logoutSuccessHandler(jwtLogoutSuccessHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}

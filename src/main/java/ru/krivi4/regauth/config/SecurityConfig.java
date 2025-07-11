package ru.krivi4.regauth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.krivi4.regauth.security.filter.JwtFilter;
import ru.krivi4.regauth.security.filter.JwtLogoutSuccessHandler;

/**
 * Конфигурация Spring Security:
 * настройка JWT-фильтра, правил доступа, stateless-сессий,
 * шифрования паролей и аутентификации пользователей.
 */
@Configuration
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String API_PREFIX = "/regauth/api/v1/auth";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String LOGOUT_URL = API_PREFIX + "/logout";
    private static final String[] ALLOWED_URLS = {
            API_PREFIX + "/login",
            API_PREFIX + "/registration",
            API_PREFIX + "/registration/verify",
            API_PREFIX + "/login/verify",
            API_PREFIX + "/refresh",
            "/error",
    };
    private static final String[] SWAGGER_OPEN_URLS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    };
    private static final String SWAGGER_CLOSED_URLS = "/v3/api-docs/**";

    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;
    private final JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

    /**
     * Настраивает AuthenticationManagerBuilder с использованием UserDetailsService и PasswordEncoder.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

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
                .antMatchers(ALLOWED_URLS).permitAll()
                .antMatchers(SWAGGER_OPEN_URLS).permitAll()
                .antMatchers(SWAGGER_CLOSED_URLS).hasRole(ROLE_ADMIN)
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

    /**
     * Создаёт бин PasswordEncoder для шифрования паролей с помощью BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создаёт бин AuthenticationManager для аутентификации пользователей.
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
package ru.krivi4.regauth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.krivi4.regauth.security.filter.JwtFilter;
import ru.krivi4.regauth.services.auth.PersonDetailsService;
import ru.krivi4.regauth.security.filter.JwtLogoutSuccessHandler;

/**
 * Конфигурация Spring Security: настройка JWT-фильтра,
 * stateless-сессий и открытых URL.
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final PersonDetailsService personDetailsService;
  private final JwtFilter jwtFilter;
  private final JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

  private final String[] ALLOWED_URLS = new String[]{
          "/api/v1/auth/login",
          "/api/v1/auth/registration",
          "/api/v1/auth/registration/verify",
          "/api/v1/auth/login/verify",
          "/api/v1/auth/refresh",
          "/error",
  };

  private final String[] SWAGGER_URLS = new String[]{
          "/swagger-ui/**",
          "/v3/api-docs/**",
          "/swagger-ui.html",
          "/swagger-resources/**",
          "/webjars/**"
  };

  public static final String ROLE_ADMIN = "ADMIN";

  /**Настраивает HTTP Security: отключение CSRF, правила доступа, logout и stateless-сессии.*/
  protected void configure(HttpSecurity http) throws Exception {

    http
      .csrf().disable()
      .authorizeRequests()
      .antMatchers(SWAGGER_URLS).hasRole(ROLE_ADMIN)
      .antMatchers(ALLOWED_URLS)
      .permitAll()
      .anyRequest().authenticated()
      .and()
      .logout()
      .logoutUrl("/api/v1/auth/logout")
      .logoutSuccessHandler(jwtLogoutSuccessHandler)
      .and()
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
  }
  /** Настраивает провайдера аутентификации с BCrypt.*/
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    auth.userDetailsService(personDetailsService).passwordEncoder(getPasswordEncoder());
}
  /**Бин для шифрования паролей BCryptPasswordEncoder.*/
  @Bean
  public PasswordEncoder getPasswordEncoder(){

    return new BCryptPasswordEncoder();
}
    /**Бин необходимый для аутентификации.*/
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {

    return super.authenticationManagerBean();
  }
}

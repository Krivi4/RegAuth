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

  /**Настраивает HTTP Security: отключение CSRF, правила доступа, logout и stateless-сессии.*/
  protected void configure(HttpSecurity http) throws Exception {

    http
        .csrf().disable()
        .authorizeRequests()
        .antMatchers("/auth/login", "/auth/registration",
        "/auth/registration/verify","/auth/login/verify","/error")
        .permitAll()
        .anyRequest().authenticated()
        .and()
        .logout()
        .logoutUrl("/logout")
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

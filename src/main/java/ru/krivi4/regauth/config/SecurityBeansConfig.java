package ru.krivi4.regauth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Конфигурация бинов для Spring Security.
 * Здесь определяются PasswordEncoder и AuthenticationManager,
 * которые используются для аутентификации и шифрования паролей.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityBeansConfig {

    private final UserDetailsService userDetailsService;

    /**
     * Создаёт бин PasswordEncoder для шифрования паролей с помощью BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создаёт бин AuthenticationManager для аутентификации пользователей.
     * Настраивается с использованием кастомного UserDetailsService и PasswordEncoder.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationManagerBuilder authenticationManagerBuilder
    ) throws Exception {
        configureAuthenticationProvider(authenticationManagerBuilder);
        return authenticationManagerBuilder.build();
    }

    /**
     * Настраивает AuthenticationManagerBuilder с использованием UserDetailsService и PasswordEncoder.
     */
    private void configureAuthenticationProvider(
            AuthenticationManagerBuilder authenticationManagerBuilder
    ) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}

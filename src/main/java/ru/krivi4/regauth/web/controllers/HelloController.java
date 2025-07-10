package ru.krivi4.regauth.web.controllers;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.krivi4.regauth.security.auth.PersonDetails;

/**
 * Заглушка-контроллер для проверки аутентификации.
 */
@RestController
public class HelloController {

    private static final String HELLO_PATH = "/hello";
    private static final String USER_INFO_PATH = "/showUserInfo";
    private static final String HELLO_MESSAGE = "Hello!";

    /**
     * Возвращает текст «Hello!».
     */
    @GetMapping(value = HELLO_PATH, produces = MediaType.TEXT_PLAIN_VALUE)
    public String sayHello() {
        return HELLO_MESSAGE;
    }

    /**
     * Возвращает имя текущего аутентифицированного пользователя.
     */
    @GetMapping(value = USER_INFO_PATH, produces = MediaType.TEXT_PLAIN_VALUE)
    public String showUserInfo() {
        return getAuthenticatedUsername();
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Получает имя пользователя из текущего контекста безопасности.
     */
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return personDetails.getUsername();
    }
}

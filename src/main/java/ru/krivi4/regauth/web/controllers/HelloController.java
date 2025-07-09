package ru.krivi4.regauth.web.controllers;

import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.krivi4.regauth.security.auth.PersonDetails;

/**
 * ЗАГЛУШКА-контроллер для быстрой проверки аутентификации.
 */
@RestController
@NoArgsConstructor
public class HelloController {

    private static final String HELLO_PATH = "/hello";
    private static final String USER_INFO_PATH = "/showUserInfo";
    private static final String HELLO_MESSAGE = "Hello!";

    private static final String TEXT_PLAIN = MediaType.TEXT_PLAIN_VALUE;

    /**
     * Вывод «Hello!».
     */
    @GetMapping(value = HELLO_PATH, produces = TEXT_PLAIN)
    public String sayHello() {
        return HELLO_MESSAGE;
    }

    /**
     * Возвращает имя аутентифицированного пользователя.
     */
    @GetMapping(value = USER_INFO_PATH, produces = TEXT_PLAIN)
    public String showUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return personDetails.getUsername();
    }
}

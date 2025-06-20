package ru.krivi4.regauth.web.controllers;

import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.krivi4.regauth.security.auth.PersonDetails;

/** Контроллер для проверки аутентификации*/
@Controller
@NoArgsConstructor
@RestController
public class HelloController {

  @GetMapping("/hello")
  public String sayHello() {
    return "Hello!";
  }
  /** Возвращает имя аутентифицированного пользователя*/
  @GetMapping("/showUserInfo")
  public String showUserInfo() {
   Authentication authentication =
     SecurityContextHolder.getContext().getAuthentication();
   PersonDetails personDetails =
    (PersonDetails) authentication.getPrincipal();

   return personDetails.getUsername();
  }
}

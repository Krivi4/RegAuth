package ru.krivi4.regauth.views;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**Пара access/refresh токенов.*/
@Getter
@Setter
@AllArgsConstructor
public class TokenResponse {

  private String accessToken;

  private String refreshToken;

}

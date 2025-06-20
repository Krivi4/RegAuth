package ru.krivi4.regauth.jwt;

import ru.krivi4.regauth.web.exceptions.PhaseUnknownException;

/**
 * Возможные фазы JWT: ожидание одноразового кода (OTP_PENDING),
 * полноценный access-токен (FULL) и refresh-токен (REFRESH).
 * */
public enum Phase {

  OTP_PENDING, FULL, REFRESH;

  /**Преобразует строковое значение claim из JWT в Phase*/
  public static Phase fromClaim(String rawPhase) {
    if(rawPhase == null || rawPhase.isEmpty()) {
      throw new IllegalArgumentException("Phase claim отсутствует");
    }
    try {
      return Phase.valueOf(rawPhase);
    } catch (IllegalArgumentException e) {
      throw new PhaseUnknownException(rawPhase);
    }
  }
}

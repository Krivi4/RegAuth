package ru.krivi4.regauth.jwt.phase;

/**
 * Возможные фазы JWT: ожидание одноразового кода (OTP_PENDING),
 * полноценный access-токен (FULL) и refresh-токен (REFRESH).
 */
public enum Phase {
    OTP_PENDING, FULL, REFRESH;
}

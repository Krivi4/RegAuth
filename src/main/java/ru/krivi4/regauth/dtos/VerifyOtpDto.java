package ru.krivi4.regauth.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO для проверки Otp: содержит UUID и код.
 */
@Getter
@Setter
public class VerifyOtpDto {

    /**
     * Уникальный идентификатор сессии Otp.
     */
    private UUID idOtp;

    /**
     * Шестизначный Otp-код.
     */
    private String code;
}

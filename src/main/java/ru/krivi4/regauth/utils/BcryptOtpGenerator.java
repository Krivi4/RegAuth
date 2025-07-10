package ru.krivi4.regauth.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.ports.otp.OtpGenerator;

import java.security.SecureRandom;

/**
 * Генератор и BCrypt-хешировщик одноразовых кодов (OTP).
 */
@Component
public class BcryptOtpGenerator implements OtpGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int OTP_MAX_VALUE = 1_000_000;
    private static final String OTP_FORMAT = "%06d";

    /**
     * Создаёт 6‑значный одноразовый код.
     */
    @Override
    public String generateCode() {
        return formatOtp(generateRandomOtp());
    }

    /**
     * Хеширует код с помощью BCrypt.
     */
    @Override
    public String hash(String raw) {
        return hashWithBCrypt(raw);
    }

    /**
     * Сравнивает введённый код с его BCrypt‑хешем.
     */
    @Override
    public boolean matches(String raw, String hash) {
        return verifyWithBCrypt(raw, hash);
    }

    /* ---------- Вспомогательные методы ---------- */

    /**
     * Генерирует случайное число для OTP.
     */
    private int generateRandomOtp() {
        return SECURE_RANDOM.nextInt(OTP_MAX_VALUE);
    }

    /**
     * Приводит число OTP к строке фиксированной длины.
     */
    private String formatOtp(int otp) {
        return String.format(OTP_FORMAT, otp);
    }

    /**
     * Хеширует строку с использованием BCrypt.
     */
    private String hashWithBCrypt(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt());
    }

    /**
     * Проверяет строку против BCrypt‑хеша.
     */
    private boolean verifyWithBCrypt(String raw, String hash) {
        return BCrypt.checkpw(raw, hash);
    }
}

package ru.krivi4.regauth.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.ports.otp.OtpGenerator;

import java.security.SecureRandom;

/**
 * Генерация и BCrypt-хеширование Otp.
 */
@Component
public class BcryptOtpGenerator implements OtpGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final int OTP_MAX_VALUE = 1_000_000;
    private static final String OTP_FORMAT = "%06d";

    /**
     * Генерирует 6‑значный одноразовый код.
     */
    @Override
    public String generateCode() {
        return String.format(OTP_FORMAT, SECURE_RANDOM.nextInt(OTP_MAX_VALUE));
    }

    /**
     * Хеширует код с помощью BCrypt.
     */
    @Override
    public String hash(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt());
    }

    /**
     * Проверяет, совпадает ли код с его BCrypt-хешем.
     */
    @Override
    public boolean matches(String raw, String hash) {
        return BCrypt.checkpw(raw, hash);
    }
}

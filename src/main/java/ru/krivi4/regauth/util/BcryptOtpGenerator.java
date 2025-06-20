package ru.krivi4.regauth.util;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import ru.krivi4.regauth.ports.otp.OtpGenerator;

import java.security.SecureRandom;
/**Генерация и BCrypt-хеширование Otp.*/
@Component
public class BcryptOtpGenerator implements OtpGenerator {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  /**Генерирует 6‑значный код.*/
  @Override
  public String generateCode() {
    return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
  }

  /**BCrypt‑хеширует код.*/
  @Override
  public String hash(String raw) {
    return BCrypt.hashpw(raw, BCrypt.gensalt());
  }

  /**Сравнивает код с хешем.*/
  @Override
  public boolean matches(String raw, String hash) {
    return BCrypt.checkpw(raw, hash);
  }
}

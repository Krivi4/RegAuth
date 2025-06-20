package ru.krivi4.regauth.ports.otp;

/**Генерация и хеширование Otp.*/
public interface OtpGenerator {

  /** Генерация шестизначного кода. */
  String generateCode();

  /** Хеширование кода. */
  String hash(String rawCode);

  /** Проверяет, соответствует ли переданный открытый код его сохранённому хешу.*/
  boolean matches(String raw, String hash);
}
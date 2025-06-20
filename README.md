RegAuth – Rest-сервис двухфакторной авторизации на Spring Boot

RegAuth — это приложение на SpringBoot, которое реализует двухфакторную аутентификацию (2FA) через одноразовые SMS‑коды(API SMS.ru). 
Сервис выдаёт OTP токен и OTP id и SMS-код и при их успешной проверки выдаёт короткоживущие access‑ и refresh‑JWT,
а также хранит отозванные токены, обновляет access и refresh токены.


О проекте
RegAuth обеспечивает безопасную регистрацию и вход пользователей через SMS-код. 
После подтверждения выдаётся пара токенов access / refresh. 
Просроченные и отозванные токены чистятся плановыми задачами, а попытки ввода OTP лимитируются.

Технический стек
Категория | Технологии
Язык      | Java 11
Framework | Spring Boot 2.7.18, Spring Security, Spring Data JPA
БД        | PostgreSQL + Liquibase 4.9.1
Внешний API | SMS.ru
Маппинг   | MapStruct 1.5.5.Final
Инструменты | Maven, Lombok, JUnit 5, Mockito

Требования
JDK 11
Maven 3.8+

Файл конфигурации
src/main/resources/application.properties
Подставте свои значения

REST API
Метод | URL | Токен | Описание
POST | /auth/registration | – | Запрос SMS-кода для регистрации
POST | /auth/registration/verify | Bearer otpToken | Подтверждение регистрации
POST | /auth/login | – | Запрос SMS-кода для входа
POST | /auth/login/verify | Bearer otpToken | Подтверждение входа
POST | /auth/refresh | Bearer refreshToken | Обновление пары токенов
GET  | /hello | Bearer accessToken | Тестовый эндпоинт
GET  | /showUserInfo | Bearer accessToken | Тестовый эндпоинт

Тестирование
mvn test          # юнит-тесты JUnit 5 + Mockito
mvn jacoco:report # HTML-отчёт покрытия в target/site/jacoco

Плановые задачи
Метод | Cron (MSK) | Цель
OtpCleanService.cleanExpiredOTP | 0 0 0 * * * | Ежедневная очистка просроченных OTP
JwtBlackListService.cleanExpiredTokens | 0 0 0 * * * | Ежедневная очистка отозванных access-токенов
RefreshTokenService.purgeExpired | 0 0 * * * * | Почасовое удаление просроченных refresh-токенов

Контакты
Автор | Рындин Артём Игоревич (Krivi4)
Email | air.@krivi4.ru
Telegram | @Kr1v14

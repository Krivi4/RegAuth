--liquibase formatted sql

--changeset krivi4:otp_codes-01
CREATE TABLE IF NOT EXISTS security.otp_codes
(
    id           UUID PRIMARY KEY,
    phone_number varchar(20)  NOT NULL,
    code_hash    varchar(100) NOT NULL,
    expires_at   timestamptz  NOT NULL,
    attempts     int DEFAULT 0
    );

COMMENT ON TABLE security.otp_codes IS 'Одноразовые коды (OTP)';

COMMENT ON COLUMN security.otp_codes.id           IS 'Идентификатор)';
COMMENT ON COLUMN security.otp_codes.phone_number IS 'Номер телефона';
COMMENT ON COLUMN security.otp_codes.code_hash    IS 'Хэш-значение OTP-кода';
COMMENT ON COLUMN security.otp_codes.expires_at   IS 'Время истечения действия кода';
COMMENT ON COLUMN security.otp_codes.attempts     IS 'Количество попыток ввода';
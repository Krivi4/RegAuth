--liquibase formatted sql

--changeset krivi4:refresh_tokens-01
CREATE TABLE IF NOT EXISTS security.refresh_tokens
(
    jti        UUID PRIMARY KEY,
    username   VARCHAR(100) NOT NULL REFERENCES users (username) ON DELETE CASCADE,
    expires_at TIMESTAMP    NOT NULL,
    revoked    BOOLEAN      NOT NULL DEFAULT FALSE
    );

COMMENT ON TABLE security.refresh_tokens IS 'Refresh-токены';

COMMENT ON COLUMN security.refresh_tokens.jti        IS 'Идентификатор токена';
COMMENT ON COLUMN security.refresh_tokens.username   IS 'Имя пользователя';
COMMENT ON COLUMN security.refresh_tokens.expires_at IS 'Дата/время истечения токена';
COMMENT ON COLUMN security.refresh_tokens.revoked    IS 'Отозван ли токен';
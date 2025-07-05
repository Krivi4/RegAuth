--liquibase formatted sql

--changeset krivi4:revoked_access_tokens-01
CREATE TABLE IF NOT EXISTS security.revoked_access_tokens
(
    jti        UUID PRIMARY KEY,
    expires_at TIMESTAMP NOT NULL
);

COMMENT ON TABLE security.revoked_access_tokens IS 'Отозванные access-токены';

COMMENT ON COLUMN security.revoked_access_tokens.jti        IS 'Идентификатор токена';
COMMENT ON COLUMN security.revoked_access_tokens.expires_at IS 'Дата и время истечения токена (после чего запись удаляется из чёрного списка)';


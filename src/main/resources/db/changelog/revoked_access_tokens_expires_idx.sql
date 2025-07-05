--liquibase formatted sql

--changeset krivi4:revoked_access_tokens_expires_idx-01
CREATE INDEX IF NOT EXISTS revoked_access_tokens_expires_idx
    ON security.revoked_access_tokens (expires_at);

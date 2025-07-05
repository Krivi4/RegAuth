--liquibase formatted sql

--changeset krivi4:refresh_expires_idx-01
CREATE INDEX IF NOT EXISTS refresh_expires_idx ON security.refresh_tokens (expires_at);

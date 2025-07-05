--liquibase formatted sql

--changeset krivi4:user_username_idx-01
CREATE INDEX IF NOT EXISTS user_username_idx
    ON security.users (username);

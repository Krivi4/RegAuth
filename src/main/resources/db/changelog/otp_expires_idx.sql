--liquibase formatted sql


--changeset krivi4:otp_expires_idx-01
CREATE INDEX IF NOT EXISTS otp_expires_idx on security.otp_codes (expires_at);
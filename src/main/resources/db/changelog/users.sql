--liquibase formatted sql

--changeset krivi4:users-01
CREATE TABLE IF NOT EXISTS security.users
(
    id           int          NOT NULL DEFAULT nextval('security.users_id_seq') PRIMARY KEY,
    username     varchar(100) NOT NULL UNIQUE,
    password     varchar(100) NOT NULL,
    email        varchar(100) NOT NULL,
    phone_number varchar(20)  NOT NULL UNIQUE,
    enabled      boolean      NOT NULL DEFAULT (false),
    created_at   timestamp             DEFAULT now(),
    last_login   timestamp
    );

COMMENT ON TABLE security.users IS 'Пользователи';

COMMENT ON COLUMN security.users.id           IS 'Идентификатор';
COMMENT ON COLUMN security.users.username     IS 'Имя пользователя';
COMMENT ON COLUMN security.users.password     IS 'Пароль пользователя';
COMMENT ON COLUMN security.users.email        IS 'E-mail пользователя';
COMMENT ON COLUMN security.users.phone_number IS 'Номер телефона пользователя ';
COMMENT ON COLUMN security.users.enabled      IS 'Флаг активности (true – учётка включена)';
COMMENT ON COLUMN security.users.created_at   IS 'Дата создания записи';
COMMENT ON COLUMN security.users.last_login   IS 'Дата последнего входа';
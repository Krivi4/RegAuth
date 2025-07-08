--liquibase formatted sql

--changeset krivi4:roles-01
create table if not exists roles (
    id   bigint primary key default nextval('roles_id_seq'),
    name varchar(32) not null unique
    );

--changeset krivi4:roles-02
-- Заполняем справочник: базовые значения
insert into roles (name) values
                             ('ADMIN'),
                             ('USER')
    on conflict (name) do nothing;
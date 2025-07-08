--liquibase formatted sql

--changeset krivi4:seq_roles-01
create sequence if not exists roles_id_seq start with 1 increment by 1;
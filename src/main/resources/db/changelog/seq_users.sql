--liquibase formatted sql

--changeset krivi4:seq-users-01
CREATE SEQUENCE IF NOT EXISTS security.users_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
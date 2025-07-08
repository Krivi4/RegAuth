--liquibase formatted sql

--changeset krivi4:person_role_person_id_idx-01
create index if not exists person_role_person_id_idx on person_role (person_id);
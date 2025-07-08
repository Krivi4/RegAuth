--liquibase formatted sql

--changeset krivi4:person_role-01
create table if not exists person_role (
    person_id integer not null,
    role_id   int not null,
    primary key (person_id, role_id),
    constraint fk_person_role_person foreign key (person_id) references users(id)  on delete cascade,
    constraint fk_person_role_role foreign key (role_id)   references roles(id)  on delete cascade
    );

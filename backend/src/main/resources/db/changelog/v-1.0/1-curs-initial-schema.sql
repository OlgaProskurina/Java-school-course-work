-- liquibase formatted sql

-- changeset proskurina-oyu:1-1
create table document
(
    id           serial primary key,
    type         varchar(256),
    organization varchar(256),
    date         timestamp,
    description  text,
    patient      varchar(256),
    status       varchar(30)
);
-- rollback drop table document
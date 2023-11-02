-- liquibase formatted sql

-- changeset proskurina-oyu:1-2
create table outbox
(
    id            serial primary key,
    payload       json
);
-- rollback drop table outbox

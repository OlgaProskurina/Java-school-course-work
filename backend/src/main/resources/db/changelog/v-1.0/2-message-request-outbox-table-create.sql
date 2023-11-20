-- liquibase formatted sql

-- changeset proskurina-oyu:1-2
create table message_request_outbox
(
    id            serial primary key,
    payload       json
);
-- rollback drop table message_request_outbox

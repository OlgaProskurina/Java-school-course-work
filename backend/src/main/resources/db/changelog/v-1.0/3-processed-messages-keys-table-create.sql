-- liquibase formatted sql

-- changeset proskurina-oyu:1-3
create table processed_messages_keys
(
    idempotent_key bigint primary key
);
-- rollback drop table processed_messages_keys
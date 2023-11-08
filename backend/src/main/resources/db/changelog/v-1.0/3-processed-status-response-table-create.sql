-- liquibase formatted sql

-- changeset proskurina-oyu:1-3
create table processed_status_response
(
    message_id varchar(256) primary key
);
-- rollback drop table processed_status_response
package ru.course.work.documents.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "processed_messages_keys")
public class MessageResponse {
    @Id
    @Column(name = "idempotent_key")
    Long idempotentKey;
}
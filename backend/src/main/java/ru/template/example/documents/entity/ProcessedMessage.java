package ru.template.example.documents.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "processed_messages")
public class ProcessedMessage {
    @Id
    @Column(name = "message_id")
    Long messageId;
}
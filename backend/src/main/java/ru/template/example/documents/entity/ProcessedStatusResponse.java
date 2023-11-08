package ru.template.example.documents.entity;

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
@Table(name = "processed_status_response")
public class ProcessedStatusResponse {
    @Id
    @Column(name = "message_id")
    String messageId;
}
package ru.template.example.documents.dto;

import lombok.Data;

@Data
public class StatusResponseDto {
    /**
     * Идентификатор сообщения.
     */
    private Long messageId;
    /**
     * Номер документа.
     */
    private Long documentId;
    /**
     * Новый статус документа.
     */
    private String status;
}
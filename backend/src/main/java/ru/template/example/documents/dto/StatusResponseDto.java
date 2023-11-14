package ru.template.example.documents.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StatusResponseDto {
    /**
     * Ключ идемпотентности сообщения.
     */
    @NotNull
    private Long idempotentKey;
    /**
     * Номер документа.
     */
    @NotNull
    private Long documentId;
    /**
     * Новый статус документа.
     */
    @NotBlank
    private String status;
}
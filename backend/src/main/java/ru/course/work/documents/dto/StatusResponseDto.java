package ru.course.work.documents.dto;

import lombok.Data;
import ru.course.work.documents.validation.StatusResponseConstraint;

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
    @StatusResponseConstraint
    private String status;
}
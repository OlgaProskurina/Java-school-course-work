package ru.template.example.documents.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StatusResponseDto {
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
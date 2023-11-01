package ru.template.example.documents.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.template.example.documents.DocumentStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {
    /**
     * Номер
     */
    private Long id;
    /**
     * Вид документа
     */
    private String type;
    /**
     * Организация
     */
    private String organization;
    /**
     * Описание
     */
    private String description;
    /**
     * Пациент
     */
    private String patient;
    /**
     * Дата документа
     */
    private LocalDate date;
    /**
     * Статус
     */
    private DocumentStatus status;
    
}

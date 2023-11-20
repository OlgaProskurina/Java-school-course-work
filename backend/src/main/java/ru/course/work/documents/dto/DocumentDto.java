package ru.course.work.documents.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.course.work.documents.DocumentStatus;

import javax.validation.constraints.NotBlank;
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
    @NotBlank
    private String type;
    /**
     * Организация
     */
    @NotBlank
    private String organization;
    /**
     * Описание
     */
    @NotBlank
    private String description;
    /**
     * Пациент
     */
    @NotBlank
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

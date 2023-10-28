package ru.template.example.documents.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.template.example.documents.DocumentStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "document")
public class Document {
    /**
     * Номер
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Вид документа
     */
    @Column
    private String type;
    /**
     * Организация
     */
    @Column
    private String organization;
    /**
     * Описание
     */
    @Column
    private String description;
    /**
     * Пациент
     */
    @Column
    private String patient;
    /**
     * Дата документа
     */
    @Column
    private LocalDate date;
    /**
     * Статус
     */
    @Column
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;
}

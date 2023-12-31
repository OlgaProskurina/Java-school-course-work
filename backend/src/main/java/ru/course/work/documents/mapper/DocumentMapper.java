package ru.course.work.documents.mapper;

import ru.course.work.documents.dto.DocumentDto;
import ru.course.work.documents.persistence.entity.Document;

public interface DocumentMapper {
    /**
     * Мапит документ на его ДТО.
     *
     * @param document документ
     * @return ДТО документа
     */
    DocumentDto toDocumentDto(Document document);
    
    /**
     * Мапит ДТО документа на его сущность.
     *
     * @param documentDto ДТО документа
     * @return документ
     */
    Document toDocument(DocumentDto documentDto);
    
}

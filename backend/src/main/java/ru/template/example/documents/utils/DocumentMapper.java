package ru.template.example.documents.utils;

import ru.template.example.documents.dto.DocumentDto;
import ru.template.example.documents.entity.Document;

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

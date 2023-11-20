package ru.course.work.documents.mapper;

import org.springframework.stereotype.Component;
import ru.course.work.documents.dto.DocumentDto;
import ru.course.work.documents.persistence.entity.Document;

@Component
public class DocumentMapperImpl implements DocumentMapper {
    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentDto toDocumentDto(Document document) {
        return DocumentDto.builder()
                          .id(document.getId())
                          .type(document.getType())
                          .organization(document.getOrganization())
                          .date(document.getDate())
                          .patient(document.getPatient())
                          .description(document.getDescription())
                          .status(document.getStatus())
                          .build();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Document toDocument(DocumentDto documentDto) {
        return Document.builder()
                       .id(documentDto.getId())
                       .type(documentDto.getType())
                       .organization(documentDto.getOrganization())
                       .date(documentDto.getDate())
                       .patient(documentDto.getPatient())
                       .description(documentDto.getDescription())
                       .status(documentDto.getStatus())
                       .build();
    }
}

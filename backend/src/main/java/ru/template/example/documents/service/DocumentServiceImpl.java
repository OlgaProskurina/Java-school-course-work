package ru.template.example.documents.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.template.example.documents.dto.DocumentDto;
import ru.template.example.documents.entity.Document;
import ru.template.example.documents.DocumentStatus;
import ru.template.example.documents.repository.DocumentRepository;
import ru.template.example.documents.utils.DocumentMapper;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 */
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    /**
     * Репозиторий для документов.
     */
    private final DocumentRepository documentRepository;
    /**
     * Маппер entity и DTO.
     */
    private final DocumentMapper documentMapper;
    /**
     * Сервис для outbox.
     */
    private final DocumentOutboxService outboxService;
    
    /**
     * Устанавливает текущую дату и статус {@code Status.NEW} для переданного документа.
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public DocumentDto save(DocumentDto documentDto) {
        Document document = documentMapper.toDocument(documentDto);
        document.setStatus(DocumentStatus.NEW);
        document.setDate(LocalDate.now());
        documentRepository.save(document);
        return documentMapper.toDocumentDto(document);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(Long id) {
        documentRepository.deleteById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public DocumentDto processDocument(Long id) {
        DocumentDto documentDto = updateStatus(id, DocumentStatus.IN_PROCESS);
        outboxService.addMessage(documentDto);
        return documentDto;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteAll(Set<Long> ids) {
        documentRepository.deleteAllByIdIn(ids);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<DocumentDto> findAll() {
        return documentRepository.findAll()
                                 .stream()
                                 .map(documentMapper::toDocumentDto)
                                 .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public DocumentDto updateStatus(Long id, DocumentStatus newStatus) {
        Document document = documentRepository.getOne(id);
        document.setStatus(newStatus);
        return documentMapper.toDocumentDto(document);
    }
    
}
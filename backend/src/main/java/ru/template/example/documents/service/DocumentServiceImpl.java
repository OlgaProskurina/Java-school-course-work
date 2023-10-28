package ru.template.example.documents.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.template.example.documents.entity.Document;
import ru.template.example.documents.DocumentStatus;
import ru.template.example.documents.repository.DocumentRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * {@inheritDoc}
 */
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    
    private final DocumentRepository documentRepository;
    
    /**
     * Устанавливает текущую дату и статус {@code Status.NEW} для переданного документа.
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Document save(Document document) {
        document.setStatus(DocumentStatus.NEW);
        document.setDate(LocalDate.now());
        return documentRepository.save(document);
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
    public Document processDocument(Long id) {
        Document document = documentRepository.getOne(id);
        document.setStatus(DocumentStatus.IN_PROCESS);
        return document;
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
    public List<Document> findAll() {
        return documentRepository.findAll();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Document getOne(Long id) {
        return documentRepository.getOne(id);
    }
    
}

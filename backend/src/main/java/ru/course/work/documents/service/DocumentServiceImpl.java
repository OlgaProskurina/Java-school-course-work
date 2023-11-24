package ru.course.work.documents.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.course.work.documents.DocumentStatus;
import ru.course.work.documents.dto.DocumentDto;
import ru.course.work.documents.exceptions.DocumentNotFoundException;
import ru.course.work.documents.exceptions.IllegalDocumentStatusException;
import ru.course.work.documents.mapper.DocumentMapper;
import ru.course.work.documents.persistence.entity.Document;
import ru.course.work.documents.persistence.repository.DocumentRepository;
import ru.course.work.documents.service.kafka.MessageRequestService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис по работе с документами
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
    private final MessageRequestService outboxService;
    
    /**
     * Устанавливает текущую дату и статус {@code Status.NEW} для переданного документа,
     * сохраняет и возвращает сохраненный документ.
     *
     * @param documentDto документ
     * @return сохраненный документ
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
     * Удалить документ по идентификатору.
     *
     * @param id идентификатор документа
     */
    @Override
    @Transactional
    public void delete(Long id) {
        documentRepository.deleteById(id);
    }
    
    /**
     * Обновляет статус документа на {@link DocumentStatus#IN_PROCESS} по идентификатору
     * и добавляет ДТО документа в исходящие сообщения.
     *
     * @param id идентификатор документа
     * @return обновленный документ
     * @throws DocumentNotFoundException      если документа с таким идентификатором не найдено
     * @throws IllegalDocumentStatusException если статус документа был не {@link DocumentStatus#NEW}
     */
    @Override
    @Transactional
    public DocumentDto processDocument(Long id) {
        Document document = requireDocument(id);
        if (!DocumentStatus.NEW.equals(document.getStatus())) {
            throw new IllegalDocumentStatusException("Document status was not " + DocumentStatus.NEW);
        }
        document.setStatus(DocumentStatus.IN_PROCESS);
        DocumentDto documentDto = documentMapper.toDocumentDto(document);
        outboxService.addMessage(documentDto);
        return documentDto;
    }
    
    /**
     * Удалить документы по идентификаторам.
     *
     * @param ids идентификаторы документов
     */
    @Override
    @Transactional
    public void deleteAll(Set<Long> ids) {
        documentRepository.deleteAllByIdIn(ids);
    }
    
    /**
     * Получить список всех документов.
     *
     * @return список документов
     */
    @Override
    @Transactional(readOnly = true)
    public List<DocumentDto> findAll() {
        return documentRepository.findAll()
                                 .stream()
                                 .map(documentMapper::toDocumentDto)
                                 .collect(Collectors.toList());
    }
    
    /**
     * Обновить статус документа по результатам обработки.
     *
     * @param id        идентификатор документа
     * @param newStatus новый статус
     * @throws DocumentNotFoundException      если документа с таким идентификатором не найдено
     * @throws IllegalDocumentStatusException если документа статус документа был не {@link DocumentStatus#IN_PROCESS}
     *                                        или {@code newStatus} был не равен {@link DocumentStatus#ACCEPTED}
     *                                        или {@link DocumentStatus#DECLINED}
     */
    @Override
    @Transactional
    public void updateStatus(Long id, DocumentStatus newStatus) {
        Document document = requireDocument(id);
        if (!DocumentStatus.IN_PROCESS.equals(document.getStatus())) {
            throw new IllegalDocumentStatusException("Document status was not " + DocumentStatus.IN_PROCESS);
        }
        if (!DocumentStatus.ACCEPTED.equals(newStatus) && !DocumentStatus.DECLINED.equals(newStatus)) {
            throw new IllegalDocumentStatusException("New status should be " + DocumentStatus.ACCEPTED +
                    " or " + DocumentStatus.DECLINED);
        }
        document.setStatus(newStatus);
    }
    
    /**
     * Возвращает документ по идентификатору.
     *
     * @param id идентификатор
     * @return найденный документ
     * @throws DocumentNotFoundException если документа с таким идентификатором не найдено
     */
    private Document requireDocument(Long id) {
        Optional<Document> documentOptional = documentRepository.findById(id);
        if (documentOptional.isEmpty()) {
            throw new DocumentNotFoundException("Document with id= " + id + " not found");
        }
        return documentOptional.get();
    }
}
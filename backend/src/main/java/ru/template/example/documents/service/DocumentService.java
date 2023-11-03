package ru.template.example.documents.service;

import ru.template.example.documents.DocumentStatus;
import ru.template.example.documents.dto.DocumentDto;

import java.util.List;
import java.util.Set;

/**
 * Сервис по работе с документами
 */
public interface DocumentService {
    /**
     * Сохраняет и возвращает сохраненный документ.
     *
     * @param documentDto документ
     * @return сохраненный документ
     */
    DocumentDto save(DocumentDto documentDto);
    
    /**
     * Удалить документы по идентификаторам.
     *
     * @param ids идентификаторы документов
     */
    void deleteAll(Set<Long> ids);
    
    /**
     * Удалить документ по идентификатору.
     *
     * @param id идентификатор документа
     */
    void delete(Long id);
    
    /**
     * Обновить статус документа на {@link DocumentStatus#IN_PROCESS} по идентификатору.
     *
     * @param id идентификатор документа
     * @return обновленный документ
     */
    DocumentDto processDocument(Long id);
    
    /**
     * Получить список всех документов.
     *
     * @return список документов
     */
    List<DocumentDto> findAll();
    
    /**
     * Обновить статус документа по идентификатору.
     *
     * @param id идентификатор
     * @param newStatus новый статус
     * @return обновленный документ
     */
    DocumentDto updateStatus(Long id, DocumentStatus newStatus);
}

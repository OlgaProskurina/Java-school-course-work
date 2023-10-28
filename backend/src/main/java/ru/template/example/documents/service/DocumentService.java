package ru.template.example.documents.service;

import ru.template.example.documents.DocumentStatus;
import ru.template.example.documents.entity.Document;

import java.util.List;
import java.util.Set;

/**
 * Сервис по работе с документами
 */
public interface DocumentService {
    /**
     * Сохраняет и возвращает сохраненный документ.
     *
     * @param document документ
     * @return сохраненный документ
     */
    Document save(Document document);
    
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
    Document processDocument(Long id);
    
    /**
     * Получить список всех документов.
     *
     * @return список документов
     */
    List<Document> findAll();
    
    /**
     * Получить документ по идентификатору.
     *
     * @param id идентификатор
     * @return документ
     */
    Document getOne(Long id);
}

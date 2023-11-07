package ru.template.example.documents.service;

import ru.template.example.documents.entity.ProcessedStatusResponse;

/**
 * Сервис по работе с входящими сообщениями {@link ProcessedStatusResponse}.
 */
public interface ProcessedStatusResponseService {
    
    
    /**
     * Возвращает {@code true} если сообщение с таким идентификатором уже было обработано.
     *
     * @param id идентификатор
     * @return возвращает {@code true} если сообщение с таким идентификатором уже было обработано
     */
    boolean existsById(Long id);
    
    /**
     * Сохраняет идентификатор сообщения в таблицу обработанных сообщений.
     *
     * @param id идентификатор
     */
    void save(Long id);
    
}
package ru.course.work.documents.service.kafka;

import ru.course.work.documents.dto.DocumentDto;


/**
 * Сервис для работы с исходящими сообщениями содержащих {@code DocumentDto}.
 */
public interface MessageRequestService {
    /**
     * Создает исходящее сообщение и сохраняет его.
     *
     * @param documentDto содержание сообщения
     */
    void addMessage(DocumentDto documentDto);
    
    /**
     * Отправляет сообщение брокеру.
     */
    void processMessages();
    
}
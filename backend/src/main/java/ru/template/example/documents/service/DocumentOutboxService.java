package ru.template.example.documents.service;

import ru.template.example.documents.dto.DocumentDto;


/**
 * Сервис для работы с исходящими сообщениями содержащих {@code DocumentDto}.
 */
public interface DocumentOutboxService {
    /**
     * Создает исходящее сообщение и сохраняет его.
     *
     * @param documentDto содержание сообщения
     */
    void addMessage(DocumentDto documentDto);
    
    /**
     * Отправляет сообщение брокеру.
     */
    void sendMessage();
    
}
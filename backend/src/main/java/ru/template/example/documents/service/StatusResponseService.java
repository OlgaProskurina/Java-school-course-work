package ru.template.example.documents.service;

import ru.template.example.documents.dto.StatusResponseDto;

/**
 * Сервис по работе с входящими сообщениями {@link ru.template.example.documents.dto.StatusResponseDto}.
 */
public interface StatusResponseService {
    
    /**
     * Обрабатывает входящее сообщение, если оно еще не было обработано и
     * сохраняет его ключ идемпотентности в таблицу обработанных сообщений.
     *
     * @param statusResponseDto входящее сообщение
     */
    void processStatusResponse(StatusResponseDto statusResponseDto);
}
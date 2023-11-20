package ru.course.work.documents.service.kafka;

import ru.course.work.documents.dto.StatusResponseDto;

/**
 * Сервис по работе с входящими сообщениями {@link StatusResponseDto}.
 */
public interface MessageResponseService {
    
    /**
     * Обрабатывает входящее сообщение, если оно еще не было обработано и
     * сохраняет его ключ идемпотентности в таблицу обработанных сообщений.
     *
     * @param statusResponseDto входящее сообщение
     */
    void processStatusResponse(StatusResponseDto statusResponseDto);
}
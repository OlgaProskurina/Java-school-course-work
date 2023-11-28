package ru.course.work.documents.service.kafka;

import ru.course.work.documents.dto.StatusResponseDto;

import java.util.Optional;

/**
 * Сервис по работе с входящими сообщениями {@link StatusResponseDto}.
 */
public interface MessageResponseService {
    
    /**
     * Обрабатывает входящее сообщение, если оно еще не было обработано и
     * сохраняет его ключ идемпотентности в таблицу обработанных сообщений.
     * Возвращает ссылку на сообщение, если оно было обработано.
     *
     * @param statusResponseDto входящее сообщение
     * @return возвращает ссылку на сообщение, если оно было обработано
     */
    Optional<StatusResponseDto> processStatusResponse(StatusResponseDto statusResponseDto);
}
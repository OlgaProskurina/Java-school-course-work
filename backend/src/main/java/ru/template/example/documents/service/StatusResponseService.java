package ru.template.example.documents.service;

import ru.template.example.documents.dto.StatusResponseDto;
import ru.template.example.documents.messaging.exception.DuplicateMessageException;

/**
 * Сервис по работе с входящими сообщениями {@link StatusResponseDto}.
 */
public interface StatusResponseService {
    /**
     * Обрабатывает входящее сообщение.
     *
     * @throws DuplicateMessageException если сообщение уже было обработано
     */
    void processResponseMessage(StatusResponseDto responseDto) throws DuplicateMessageException;
}
package ru.course.work.documents.service.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import ru.course.work.documents.dto.DocumentDto;

import java.util.Optional;


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
    Optional<ProducerRecord<String, Object>> processMessages();
    
}
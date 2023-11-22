package ru.course.work.documents.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.course.work.documents.dto.StatusResponseDto;

import javax.validation.Valid;

/**
 * Класс-консюмер сообщений из кафки.
 */
@Component
@RequiredArgsConstructor
public class StatusResponseConsumer {
    /**
     * Сервис для обработанных сообщений о статусах.
     */
    private final MessageResponseService messageResponseService;
  
    /**
     * Получает сообщения из топика {@code response-document}.
     *
     * @param payload полученное сообщение
     */
    @KafkaListener(topics = "${kafka.topic.response-document}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(@Payload @Valid StatusResponseDto payload,
                       Acknowledgment acknowledgment) {
        messageResponseService.processStatusResponse(payload);
        acknowledgment.acknowledge();
    }
    
}
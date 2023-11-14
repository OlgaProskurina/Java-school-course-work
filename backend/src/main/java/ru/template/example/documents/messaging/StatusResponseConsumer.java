package ru.template.example.documents.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.template.example.documents.dto.StatusResponseDto;
import ru.template.example.documents.service.StatusResponseService;

/**
 * Класс-консюмер сообщений из кафки.
 */
@Component
@RequiredArgsConstructor
public class StatusResponseConsumer {
    /**
     * Сервис для обработанных сообщений о статусах.
     */
    private final StatusResponseService statusResponseService;
  
    /**
     * Получает сообщения из топика {@code response-document}.
     *
     * @param payload полученное сообщение
     */
    @KafkaListener(topics = "${kafka.topic.response-document}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(@Payload StatusResponseDto payload,
                       Acknowledgment acknowledgment) {
        statusResponseService.processStatusResponse(payload);
        acknowledgment.acknowledge();
    }
    
}
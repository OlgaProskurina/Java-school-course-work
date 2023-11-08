package ru.template.example.documents.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
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
     * @param messageKey идентификатор сообщения
     */
    @KafkaListener(topics = "${kafka.topic.response-document}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(@Payload StatusResponseDto payload,
                       @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String messageKey,
                       Acknowledgment acknowledgment) {
        statusResponseService.processStatusResponse(messageKey, payload);
        acknowledgment.acknowledge();
    }
    
}
package ru.template.example.documents.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.template.example.documents.dto.StatusResponseDto;
import ru.template.example.documents.messaging.exception.DuplicateMessageException;
import ru.template.example.documents.service.StatusResponseService;

@Component
@RequiredArgsConstructor
public class StatusResponseConsumer {
    
    private final StatusResponseService responseService;
    @KafkaListener(topics = "${kafka.topic.response-document}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(@Payload StatusResponseDto payload) {
        try {
            responseService.processResponseMessage(payload);
        } catch (DuplicateMessageException e) {
            System.err.println(e.getMessage());
        }
    }
}

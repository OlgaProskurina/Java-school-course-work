package ru.template.example.documents.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.util.concurrent.ListenableFuture;
import ru.template.example.documents.dto.DocumentDto;
import ru.template.example.documents.entity.OutboxMessage;
import ru.template.example.documents.repository.OutboxRepository;

import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Сервис для работы с исходящими сообщениями содержащих {@code DocumentDto}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentOutboxServiceImpl implements DocumentOutboxService {
    /**
     * Топик для отправки сообщений.
     */
    @Value(value = "${kafka.topic.process-document}")
    private String topic;
    /**
     * Для маппинга ДТО в {@code JsonNode}.
     */
    private final ObjectMapper objectMapper;
    /**
     * Репозиторий для исходящих сообщений.
     */
    private final OutboxRepository outboxRepository;
    /**
     * Для отправки сообщений брокеру.
     */
    private final KafkaTemplate<String, JsonNode> kafkaTemplate;
    
    /**
     * Создает исходящее сообщение и сохраняет его.
     *
     * @param documentDto содержание сообщения
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addMessage(DocumentDto documentDto) {
        OutboxMessage message = new OutboxMessage();
        message.setPayload(objectMapper.convertValue(documentDto, JsonNode.class));
        outboxRepository.save(message);
    }
    
    /**
     * Пытается найти не отправленное сообщение, если такое существует, то отправляет его в кафку.
     * Если удалось отправить сообщение удаляет его из таблицы.
     */
    @Override
    @Transactional
    public void sendMessage() {
        Optional<OutboxMessage> messageOptional = outboxRepository.getMessage();
        if(messageOptional.isPresent()) {
            var outboxMessage = messageOptional.get();
            ListenableFuture<SendResult<String, JsonNode>> future =
                    kafkaTemplate.send(topic, outboxMessage.getPayload());
            try {
                future.get();
                outboxRepository.deleteById(outboxMessage.getId());
            } catch (InterruptedException | ExecutionException e) {
                log.error("PRODUCER ERROR: Не удалось отправить сообщение." + e.getMessage());
            }
        }
    }
    
}
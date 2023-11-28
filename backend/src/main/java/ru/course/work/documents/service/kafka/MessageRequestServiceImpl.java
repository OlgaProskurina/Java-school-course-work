package ru.course.work.documents.service.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.course.work.documents.dto.DocumentDto;
import ru.course.work.documents.persistence.entity.MessageRequest;
import ru.course.work.documents.persistence.repository.MessageRequestRepository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Сервис для работы с исходящими сообщениями содержащих {@code DocumentDto}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageRequestServiceImpl implements MessageRequestService {
    /**
     * Топик для отправки сообщений.
     */
    @Value(value = "${kafka.topic.process-document}")
    private String requestTopic;
    /**
     * Для маппинга ДТО в {@code JsonNode}.
     */
    private final ObjectMapper objectMapper;
    /**
     * Репозиторий для исходящих сообщений.
     */
    private final MessageRequestRepository messageRequestRepository;
    /**
     * Для отправки сообщений брокеру.
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    /**
     * Создает исходящее сообщение и сохраняет его.
     *
     * @param documentDto содержание сообщения
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addMessage(DocumentDto documentDto) {
        MessageRequest message = new MessageRequest();
        message.setPayload(objectMapper.convertValue(documentDto, JsonNode.class));
        messageRequestRepository.save(message);
    }
    
    /**
     * Пытается найти не отправленное сообщение, если такое существует, то отправляет его в кафку.
     * Если удалось отправить сообщение удаляет его из таблицы.
     * Возвращает ссылку на отправленный {@code ProducerRecord<String, Object>}.
     *
     * @return возвращает ссылку на отправленный {@code ProducerRecord<String, Object>}
     */
    @Override
    @Transactional
    public Optional<ProducerRecord<String, Object>> processMessages() {
        Optional<MessageRequest> messageRequestOptional = messageRequestRepository.findUnsentMessage();
        if (messageRequestOptional.isPresent()) {
            var messageRequest = messageRequestOptional.get();
            log.debug("Sending MessageRequest id={} to {}", messageRequest.getId(), requestTopic);
            try {
                var sendResult = kafkaTemplate.send(requestTopic, messageRequest.getPayload()).get();
                messageRequestRepository.deleteById(messageRequest.getId());
                log.debug("Message sent successful to {} metadata {}. Deleted from outbox",
                        requestTopic, sendResult.getRecordMetadata());
                return Optional.of(sendResult.getProducerRecord());
            } catch (InterruptedException | ExecutionException e) {
                log.error("PRODUCER ERROR: Failed to send message to {} exception {}",
                        requestTopic, e.getMessage());
            }
            
        }
        return Optional.empty();
    }
    
}
package ru.course.work.documents.service.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
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
    private String topic;
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
    private final KafkaTemplate<String, JsonNode> kafkaTemplate;
    
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
     */
    @Override
    @Transactional
    public void processMessages() {
        Optional<MessageRequest> messageRequestOptional = messageRequestRepository.findUnsentMessage();

        if(messageRequestOptional.isPresent()) {
            var messageRequest = messageRequestOptional.get();
            ListenableFuture<SendResult<String, JsonNode>> future =
                    kafkaTemplate.send(topic, messageRequest.getPayload());
            future.addCallback(new ListenableFutureCallback<>() {
                @Override
                public void onFailure(@NonNull Throwable ex) {
                    log.error("PRODUCER ERROR: Не удалось отправить сообщение, exception {}", ex.getLocalizedMessage());
                }
                
                @Override
                public void onSuccess(SendResult<String, JsonNode> result) {
                    messageRequestRepository.deleteById(messageRequest.getId());
                }
            });
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("PRODUCER ERROR: Не удалось отправить сообщение." + e.getLocalizedMessage());
            }
        }
    }
    
}
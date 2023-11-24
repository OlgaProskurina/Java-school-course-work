package ru.course.work.configuration.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerAwareErrorHandler;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.course.work.documents.dto.DlqMessageResponseDto;
import ru.course.work.documents.dto.StatusResponseDto;

/**
 * Обработчик исключений, возникших во время работы консюмера.
 */
@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class KafkaErrorHandler implements ConsumerAwareErrorHandler {
    
    /**
     * Топик для отправки сообщений.
     */
    @Value(value = "${kafka.topic.dlq}")
    private String dlqTopic;
    /**
     * Для маппинга ДТО в {@code JsonNode}.
     */
    private final ObjectMapper objectMapper;
    /**
     * Для отправки сообщений в DLQ.
     */
    private final KafkaTemplate<String, JsonNode> kafkaTemplate;
    
    /**
     * Обрабатывает исключения, возникшие во время работы консюмера,
     * если причиной ошибки было не {@code DeserializationException} и {@code MethodArgumentNotValidException},
     * то отправляет {@code data.value()} в DLQ.
     *
     * @param thrownException исключение
     * @param data            запись из кафки
     * @param consumer        консюмер
     */
    @Override
    public void handle(Exception thrownException, ConsumerRecord<?, ?> data, Consumer<?, ?> consumer) {
        seek(data, consumer);
        
        Throwable cause = thrownException.getCause();
        String logMessage = "CONSUMER ERROR: Skip message in " + data.topic() + " offset " + data.offset() +
                " partition " + data.partition() + " exception " + cause;
        
        if (cause instanceof DeserializationException) {
            var deserializationException = (DeserializationException) cause;
            String malformedMessage = new String(deserializationException.getData());
            log.error(logMessage + " data " + malformedMessage);
            
        } else if (cause instanceof MethodArgumentNotValidException) {
            var notValidException = (MethodArgumentNotValidException) cause;
            StringBuilder errors = new StringBuilder();
            for (FieldError error : notValidException.getBindingResult().getFieldErrors()) {
                errors.append(error.getField()).append(": ").append(error.getDefaultMessage());
            }
            log.error(logMessage + " validation errors " + errors);
        } else {
            log.error(logMessage + " sending to DLQ");
            sendToDlq(data.value(), thrownException);
        }
    }
    
    /**
     * Сохраняет сообщение и исключение, из-за которого не удалось его обработать, в {@code DlqMessageResponseDto}
     * и отправляет в топик DLQ.
     *
     * @param value           сообщение
     * @param thrownException возникшее исключение
     */
    private void sendToDlq(Object value, Exception thrownException) {
        DlqMessageResponseDto dlqDto = new DlqMessageResponseDto();
        dlqDto.setErrorMessage(thrownException.getMessage());
        dlqDto.setStatusResponse((StatusResponseDto) value);
        
        var record = new ProducerRecord<String, JsonNode>(dlqTopic, objectMapper.convertValue(dlqDto, JsonNode.class));
        ListenableFuture<SendResult<String, JsonNode>> future = kafkaTemplate.send(record);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(@NonNull Throwable ex) {
                log.error("DLQ PRODUCER ERROR: Failed to send message {} exception {} ", dlqDto, ex.getMessage());
            }
            
            @Override
            public void onSuccess(SendResult<String, JsonNode> result) {
                log.debug("Message sent successful to DLQ metadata {}", result.getRecordMetadata());
            }
        });
        
    }
    
    /**
     * Пропускает одно сообщение.
     *
     * @param data     запись из кафки
     * @param consumer консюмер
     */
    private void seek(ConsumerRecord<?, ?> data, Consumer<?, ?> consumer) {
        TopicPartition topicPartition = new TopicPartition(data.topic(), data.partition());
        consumer.seek(topicPartition, data.offset() + 1L);
    }
    
}
package ru.template.example.configuration.messaging;

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
import ru.template.example.documents.dto.DlqMessageResponseDto;
import ru.template.example.documents.dto.StatusResponseDto;

import java.util.Optional;

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
     * Для отправки сообщений в DQL.
     */
    private final KafkaTemplate<String, JsonNode> kafkaTemplate;
    
    
    /**
     * Обрабатывает исключения, возникшие во время работы консюмера,
     * если причиной ошибки было не {@code DeserializationException},
     * то отправляет {@code data.value()} в DLQ.
     *
     * @param thrownException исключение
     * @param data            запись из кафки
     * @param consumer        консюмер
     */
    @Override
    public void handle(Exception thrownException, ConsumerRecord<?, ?> data, Consumer<?, ?> consumer) {
        seek(data, consumer);
        String topic = data.topic();
        long offset = data.offset();
        int partition = data.partition();
        Optional<DeserializationException> deserializationException = isDeserializationException(thrownException);
        if (deserializationException.isPresent()) {
            DeserializationException exception = deserializationException.get();
            String malformedMessage = new String(exception.getData());
            log.error("CONSUMER ERROR: Пропуск сообщения в {} offset {} partition {} - data: {} , причина: {}",
                    topic, offset, partition, malformedMessage, exception.getMessage());
            
        } else {
            log.error("CONSUMER ERROR: Пропуск сообщения в {} - offset {} - partition {}, Отправка в DLQ причина {}",
                    topic, offset, partition, thrownException.getMessage());
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
    public void sendToDlq(Object value, Exception thrownException) {
        DlqMessageResponseDto dlqDto = new DlqMessageResponseDto();
        dlqDto.setErrorMessage(thrownException.getMessage());
        dlqDto.setStatusResponse((StatusResponseDto) value);
        
        var record = new ProducerRecord<String, JsonNode>(dlqTopic, objectMapper.convertValue(dlqDto, JsonNode.class));
        ListenableFuture<SendResult<String, JsonNode>> future = kafkaTemplate.send(record);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(@NonNull Throwable ex) {
                log.error("DQL PRODUCER ERROR: Не удалось отправить сообщение {}, exception {} ",
                        dlqDto, ex.getMessage());
            }
            
            @Override
            public void onSuccess(SendResult<String, JsonNode> result) {
                log.debug("Сообщение успешно отправлено metadata {}", result.getRecordMetadata());
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
    
    /**
     * Ссылку на {@code DeserializationException} если оно было причиной исключения.
     *
     * @param thrownException исключение
     * @return ссылку на {@code DeserializationException} если оно было причиной исключения.
     */
    private Optional<DeserializationException> isDeserializationException(Exception thrownException) {
        if (thrownException instanceof DeserializationException) {
            return Optional.of((DeserializationException) thrownException);
        }
        if (thrownException.getCause() != null &&
            thrownException.getCause() instanceof DeserializationException) {
            return Optional.of((DeserializationException) thrownException.getCause());
        }
        return Optional.empty();
    }
    
}
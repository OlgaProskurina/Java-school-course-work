package ru.course.work.configuration.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
     * DLQ топик.
     */
    @Value(value = "${kafka.topic.dlq}")
    private String dlqTopic;
    /**
     * Для отправки сообщений в DLQ.
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    /**
     * Логирует исключения, возникшие во время работы консюмера, и если причиной были
     * {@code DeserializationException} или {@code MethodArgumentNotValidException},
     * отправляет {@code data.value()} и {@code thrownException} в DLQ.
     *
     * @param thrownException исключение
     * @param data            запись из кафки
     * @param consumer        консюмер
     */
    @Override
    public void handle(Exception thrownException, ConsumerRecord<?, ?> data, Consumer<?, ?> consumer) {
        seek(data, consumer);
        Throwable cause = thrownException.getCause();
        log.error("CONSUMER ERROR: Skip message in topic {} offset {} partition {} due to exception {}",
                data.topic(), data.offset(), data.partition(), cause.getMessage());
        
        if (cause instanceof DeserializationException) {
            var deserializationException = (DeserializationException) cause;
            String malformedMessage = new String(deserializationException.getData());
            sendToDlq(data.value(), cause.getMessage() + " data " + malformedMessage);
        } else if (cause instanceof MethodArgumentNotValidException) {
            var notValidException = (MethodArgumentNotValidException) cause;
            StringBuilder fieldErrors = new StringBuilder();
            for (FieldError error : notValidException.getBindingResult().getFieldErrors()) {
                fieldErrors.append(error.getField()).append(": ").append(error.getDefaultMessage());
            }
            sendToDlq(data.value(), cause.getMessage() + " field errors " + fieldErrors);
        }
        
    }
    
    /**
     * Оборачивает {@code value} и {@code exceptionMessage} в {@code DlqMessageResponseDto} и отправляет в топик DLQ.
     *
     * @param payload          сообщение, полученное консюмером
     * @param exceptionMessage сообщение с ошибкой
     */
    private void sendToDlq(Object payload, String exceptionMessage) {
        DlqMessageResponseDto dlqDto = new DlqMessageResponseDto();
        dlqDto.setErrorMessage(exceptionMessage);
        dlqDto.setStatusResponse((StatusResponseDto) payload);
        log.error("Sending to DLQ due to exception {}", exceptionMessage);
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(dlqTopic, dlqDto);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(@NonNull Throwable ex) {
                log.error("DLQ PRODUCER ERROR: Failed to send message {} due to exception {}", dlqDto, ex.getMessage());
            }
            
            @Override
            public void onSuccess(SendResult<String, Object> result) {
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
package ru.template.example.configuration.messaging;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.listener.ConsumerAwareErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Обработчик исключений, возникших во время работы консюмера.
 */
@Slf4j
@Primary
@Component
@NoArgsConstructor
public class KafkaErrorHandler implements ConsumerAwareErrorHandler {
    
    /**
     * Обрабатывает исключения, возникшие во время работы консюмера.
     *
     * @param thrownException исключение
     * @param data запись из кафки
     * @param consumer консюмер
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
                    topic, offset, partition, malformedMessage, exception.getLocalizedMessage());
            
        }  else {
            log.error("CONSUMER ERROR: Пропуск сообщения в {} - offset {} - partition {} - причина: {}",
                    topic, offset, partition, thrownException.getLocalizedMessage());
        }
    }
    
    /**
     * Пропускает одно сообщение.
     *
     * @param data запись из кафки
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
            return Optional.of((DeserializationException)thrownException);
        }
        if (thrownException.getCause() != null &&
            thrownException.getCause() instanceof DeserializationException) {
            return Optional.of((DeserializationException)thrownException.getCause());
        }
        return Optional.empty();
    }

}
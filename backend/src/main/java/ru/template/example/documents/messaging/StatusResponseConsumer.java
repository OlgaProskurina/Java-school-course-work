package ru.template.example.documents.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.lang.NonNull;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import ru.template.example.documents.DocumentStatus;
import ru.template.example.documents.dto.StatusResponseDto;
import ru.template.example.documents.messaging.exception.InvalidPayloadException;
import ru.template.example.documents.service.DocumentService;
import ru.template.example.documents.service.ProcessedStatusResponseService;

/**
 * Класс-консюмер сообщений из кафки.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatusResponseConsumer {
    /**
     * Сервис для обработанных сообщений о статусах.
     */
    private final ProcessedStatusResponseService processedStatusResponseService;
    /**
     * Сервис для документов.
     */
    private final DocumentService documentService;
    /**
     * Менеджер транзакций.
     */
    private final PlatformTransactionManager transactionManager;
    
    /**
     * Получает сообщение и проверяет было ли оно обработано, если да - пропускает его,
     * если нет, то обрабатывает сообщение.
     *
     * @param payload полученное сообщение
     * @throws InvalidPayloadException если статус из {@link  StatusResponseDto}
     *                                 не равен {@code DECLINED} или {@code ACCEPTED}
     */
    @KafkaListener(topics = "${kafka.topic.response-document}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(@Payload StatusResponseDto payload,
                       Acknowledgment acknowledgment) {
        
        Long messageId = payload.getMessageId();
        if (processedStatusResponseService.existsById(messageId)) {
            log.warn("CONSUMER WARN: Сообщение уже обработано:{}", payload);
            acknowledgment.acknowledge();
            return;
        }
        
        String newStatus = payload.getStatus();
        if (!DocumentStatus.ACCEPTED.getCode().equals(newStatus) && !DocumentStatus.DECLINED.getCode().equals(newStatus)) {
            throw new InvalidPayloadException("CONSUMER ERROR: Ошибка в " + payload + ": " +
                    "Значение статуса может быть " + DocumentStatus.DECLINED + " или " + DocumentStatus.ACCEPTED);
        }
        processPayloadInTransaction(payload, messageId);
        acknowledgment.acknowledge();
    }
    
    /**
     * В рамках одной транзакции сохраняет обработанное сообщение и обновляет статус документа.
     *
     * @param payload   полученное сообщение
     * @param messageId идентификатор сообщения
     */
    private void processPayloadInTransaction(StatusResponseDto payload, Long messageId) {
        
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
                processedStatusResponseService.save(messageId);
                documentService.updateStatus(payload.getDocumentId(),
                        DocumentStatus.valueOf(payload.getStatus()));
            }
        });
    }
}
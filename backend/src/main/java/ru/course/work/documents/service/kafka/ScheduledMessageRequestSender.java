package ru.course.work.documents.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Класс для периодической отправки сообщений {@link MessageRequestService}.
 */
@Component
@RequiredArgsConstructor
public class ScheduledMessageRequestSender {
    /**
     * Сервис для исходящих сообщений.
     */
    private final MessageRequestService messageRequestService;
    
    /**
     * Периодически обращается к {@code outboxService} для отправки сообщений брокеру.
     */
    @Scheduled(initialDelayString = "${outbox.scheduler.initialDelayMillis:5000}",
            fixedDelayString = "${outbox.scheduler.fixedDelayMillis:5000}")
    public void sendMessages() {
        messageRequestService.processMessages();
    }
}
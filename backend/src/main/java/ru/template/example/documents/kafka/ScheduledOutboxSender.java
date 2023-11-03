package ru.template.example.documents.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.template.example.documents.service.DocumentOutboxService;

/**
 * Класс для периодической отправки сообщений {@link DocumentOutboxService}.
 */
@Component
@RequiredArgsConstructor
public class ScheduledOutboxSender {
    /**
     * Сервис для исходящих сообщений.
     */
    private final DocumentOutboxService outboxService;
    
    /**
     * Периодически обращается к {@code outboxService} для отправки сообщений брокеру.
     */
    @Scheduled(initialDelayString = "${outbox.scheduler.initialDelayMillis:5000}",
            fixedDelayString = "${outbox.scheduler.fixedDelayMillis:5000}")
    public void sendMessages() {
        outboxService.sendMessage();
    }
}
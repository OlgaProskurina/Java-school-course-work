package ru.course.work.documents.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.course.work.documents.DocumentStatus;
import ru.course.work.documents.dto.StatusResponseDto;
import ru.course.work.documents.persistence.entity.MessageResponse;
import ru.course.work.documents.exceptions.IllegalDocumentStatusException;
import ru.course.work.documents.persistence.repository.MessageResponseRepository;

import org.springframework.transaction.annotation.Transactional;
import ru.course.work.documents.service.DocumentService;


/**
 * Сервис по работе с входящими сообщениями {@link StatusResponseDto}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageResponseServiceImpl implements MessageResponseService {
    
    /**
     * Репозиторий для обработанных сообщений.
     */
    private final MessageResponseRepository messageResponseRepository;
    /**
     * Сервис для документов.
     */
    private final DocumentService documentService;
    
    /**
     * Проверяет было ли обработано сообщение, если нет,
     * то сохраняет его ключ идемпотентности в таблицу обработанных сообщений
     * и обновляет статус документа на статус из {@code StatusResponseDto}.
     *
     * @param statusResponseDto сообщение
     * @throws IllegalDocumentStatusException если статус из {@link  StatusResponseDto}
     *                                        не равен {@code DECLINED} или {@code ACCEPTED}
     */
    @Override
    @Transactional
    public void processStatusResponse(StatusResponseDto statusResponseDto) {
        if (messageResponseRepository.existsById(statusResponseDto.getIdempotentKey())) {
            log.warn("CONSUMER WARN: StatusResponseDto уже обработано idempotentKey={}",
                    statusResponseDto.getIdempotentKey());
            return;
        }
        
        log.debug("Начата обработка StatusResponseDto idempotentKey={}",statusResponseDto.getIdempotentKey());
        String newStatus = statusResponseDto.getStatus();
        if (!DocumentStatus.ACCEPTED.getCode().equals(newStatus) && !DocumentStatus.DECLINED.getCode().equals(newStatus)) {
            throw new IllegalDocumentStatusException("Ошибка в " + statusResponseDto + ": " +
                    "Значение статуса может быть " + DocumentStatus.DECLINED + " или " + DocumentStatus.ACCEPTED);
        }
        
        messageResponseRepository.save(new MessageResponse(statusResponseDto.getIdempotentKey()));
        documentService.updateStatus(statusResponseDto.getDocumentId(), DocumentStatus.valueOf(newStatus));
        
        log.debug("Обработан StatusResponseDto idempotentKey={}",statusResponseDto.getIdempotentKey());
    }
    
}
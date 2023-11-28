package ru.course.work.documents.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.course.work.documents.DocumentStatus;
import ru.course.work.documents.dto.StatusResponseDto;
import ru.course.work.documents.exceptions.IllegalDocumentStatusException;
import ru.course.work.documents.persistence.entity.MessageResponse;
import ru.course.work.documents.persistence.repository.MessageResponseRepository;
import ru.course.work.documents.service.DocumentService;

import java.util.Optional;


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
     * Возвращает ссылку на сообщение, если оно было обработано.
     *
     * @param statusResponseDto сообщение
     * @return возвращает ссылку на сообщение, если оно было обработано
     * @throws IllegalDocumentStatusException если статус из {@link  StatusResponseDto}
     *                                        не равен {@code DECLINED} или {@code ACCEPTED}
     */
    @Override
    @Transactional
    public Optional<StatusResponseDto> processStatusResponse(StatusResponseDto statusResponseDto) {
        if (messageResponseRepository.existsById(statusResponseDto.getIdempotentKey())) {
            log.warn("CONSUMER WARN: StatusResponseDto is already processed idempotentKey={}",
                    statusResponseDto.getIdempotentKey());
            return Optional.empty();
        }
        log.debug("Processing StatusResponseDto idempotentKey={}", statusResponseDto.getIdempotentKey());
        
        messageResponseRepository.save(new MessageResponse(statusResponseDto.getIdempotentKey()));
        documentService.updateStatus(statusResponseDto.getDocumentId(),
                DocumentStatus.valueOf(statusResponseDto.getStatus()));
        
        log.debug("Processed StatusResponseDto idempotentKey={}", statusResponseDto.getIdempotentKey());
        return Optional.of(statusResponseDto);
    }
    
}
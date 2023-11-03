package ru.template.example.documents.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.template.example.documents.DocumentStatus;
import ru.template.example.documents.dto.StatusResponseDto;
import ru.template.example.documents.entity.ProcessedMessage;
import ru.template.example.documents.messaging.exception.DuplicateMessageException;
import ru.template.example.documents.repository.ProcessedMessageRepository;

import javax.transaction.Transactional;

/**
 * {@inheritDoc}
 */
@Service
@RequiredArgsConstructor
public class StatusResponseServiceImpl implements StatusResponseService {
    
    /**
     * Репозиторий для обработанных сообщений.
     */
    private final ProcessedMessageRepository processedMessageRepository;
    /**
     * Сервис для обновления статуса документа.
     */
    private final DocumentService documentService;
    
    /**
     * Ищет идентификатор сообщения в таблице обработанных сообщений, если не находит, то
     * добавляет его в таблицу и обновляет статус документа по идентификатору из {@code responseDto}.
     * <p>Если сообщение уже было обработано выбрасывает {@code DuplicateMessageException}.
     *
     * @throws DuplicateMessageException {@inheritDoc}
     */
    @Override
    @Transactional
    public void processResponseMessage(StatusResponseDto responseDto) throws DuplicateMessageException {
        Long messageId = responseDto.getMessageId();
        if (processedMessageRepository.existsById(messageId)) {
            throw new DuplicateMessageException("Сообщение уже обработано id:" + messageId);
        }
        
        ProcessedMessage message = new ProcessedMessage();
        message.setMessageId(messageId);
        processedMessageRepository.save(message);
        
        DocumentStatus documentStatus = DocumentStatus.valueOf(responseDto.getStatus());
        documentService.updateStatus(responseDto.getDocumentId(), documentStatus);
    }
}
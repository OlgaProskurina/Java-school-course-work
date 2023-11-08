package ru.template.example.documents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.template.example.documents.DocumentStatus;
import ru.template.example.documents.dto.StatusResponseDto;
import ru.template.example.documents.entity.ProcessedStatusResponse;
import ru.template.example.documents.expeptions.IllegalDocumentStatusException;
import ru.template.example.documents.repository.ProcessedStatusResponseRepository;

import org.springframework.transaction.annotation.Transactional;


/**
 * Сервис по работе с входящими сообщениями {@link StatusResponseDto}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatusResponseServiceImpl implements StatusResponseService {
    
    /**
     * Репозиторий для обработанных сообщений.
     */
    private final ProcessedStatusResponseRepository processedStatusResponseRepository;
    /**
     * Сервис для документов.
     */
    private final DocumentService documentService;
    
    /**
     * Проверяет было ли обработано сообщение, если нет,
     * то сохраняет его идентификатор в таблицу обработанных сообщений
     * и обновляет статус документа на статус из {@code StatusResponseDto}.
     *
     * @param statusResponseDto сообщение
     * @param messageKey идентификатор сообщения
     * @throws IllegalDocumentStatusException если статус из {@link  StatusResponseDto}
     *                                        не равен {@code DECLINED} или {@code ACCEPTED}
     */
    @Override
    @Transactional
    public void processStatusResponse(String messageKey, StatusResponseDto statusResponseDto) {
        if (processedStatusResponseRepository.existsById(messageKey)) {
            log.warn("CONSUMER WARN: Сообщение уже обработано id={} payload={}", messageKey, statusResponseDto);
            return;
        }
        
        String newStatus = statusResponseDto.getStatus();
        if (!DocumentStatus.ACCEPTED.getCode().equals(newStatus) && !DocumentStatus.DECLINED.getCode().equals(newStatus)) {
            throw new IllegalDocumentStatusException("Ошибка в " + statusResponseDto + ": " +
                    "Значение статуса может быть " + DocumentStatus.DECLINED + " или " + DocumentStatus.ACCEPTED);
        }
        
        processedStatusResponseRepository.save(new ProcessedStatusResponse(messageKey));
        documentService.updateStatus(statusResponseDto.getDocumentId(), DocumentStatus.valueOf(newStatus));
        
    }
    
}
package ru.template.example.documents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.template.example.documents.entity.ProcessedStatusResponse;
import ru.template.example.documents.repository.ProcessedMessageRepository;

import org.springframework.transaction.annotation.Transactional;


/**
 * Сервис по работе с входящими сообщениями {@link ProcessedStatusResponse}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessedStatusResponseServiceImpl implements ProcessedStatusResponseService {
    
    /**
     * Репозиторий для обработанных сообщений.
     */
    private final ProcessedMessageRepository processedMessageRepository;
    
    /**
     * Возвращает {@code true} если сообщение с таким идентификатором уже было обработано.
     *
     * @param id идентификатор
     * @return возвращает {@code true} если сообщение с таким идентификатором уже было обработано
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return processedMessageRepository.existsById(id);
    }
    
    /**
     * Сохраняет идентификатор сообщения в таблицу обработанных сообщений.
     *
     * @param id идентификатор
     */
    @Override
    @Transactional
    public void save(Long id) {
        ProcessedStatusResponse processed = new ProcessedStatusResponse();
        processed.setMessageId(id);
        processedMessageRepository.save(processed);
    }
    
}
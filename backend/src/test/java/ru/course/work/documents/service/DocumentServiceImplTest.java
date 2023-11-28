package ru.course.work.documents.service;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import ru.course.work.documents.DocumentStatus;
import ru.course.work.documents.dto.DocumentDto;
import ru.course.work.documents.exceptions.DocumentNotFoundException;
import ru.course.work.documents.exceptions.IllegalDocumentStatusException;
import ru.course.work.documents.mapper.DocumentMapperImpl;
import ru.course.work.documents.persistence.entity.Document;
import ru.course.work.documents.persistence.repository.DocumentRepository;
import ru.course.work.documents.service.kafka.MessageRequestServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Класс, тестирующий {@code DocumentServiceImpl}.
 */
@RunWith(SpringRunner.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = {DocumentServiceImpl.class, DocumentMapperImpl.class})
public class DocumentServiceImplTest {
    
    @Autowired
    private DocumentServiceImpl documentService;

    @MockBean
    private DocumentRepository documentRepository;
    
    @MockBean
    private MessageRequestServiceImpl messageRequestService;
    
    @Autowired
    private DocumentMapperImpl mapper;
    
    @Test
    @DisplayName("Тестируется успешный вызов findAll()")
    public void testFindAllSuccess() {
        Document documentEntity = createDocumentEntity();
        List<DocumentDto> expectedList = List.of(mapper.toDocumentDto(documentEntity));
        
        when(documentRepository.findAll()).thenReturn(List.of(documentEntity));
        
        List<DocumentDto> foundDocuments = documentService.findAll();
        
        assertThat(foundDocuments).isEqualTo(expectedList);
    }
    
    @Test
    @DisplayName("Тестируется успешное сохранение документа при вызове save(DocumentDto)")
    public void testSaveSuccess() {
        Document documentEntity = createDocumentEntity();
        DocumentDto expected = mapper.toDocumentDto(documentEntity);
        
        when(documentRepository.save(any())).thenReturn(documentEntity);
        
        DocumentDto saved = documentService.save(expected);
        
        assertThat(saved).isEqualTo(expected);
    }
    
    @Test
    @DisplayName("Тестируется успешная отправка документа в обработку при вызове processDocument(ID)")
    public void testProcessDocumentSuccess() {
        Document documentEntity = createDocumentEntity();
        when(documentRepository.findById(any())).thenReturn(Optional.of(documentEntity));
        DocumentDto expected = mapper.toDocumentDto(documentEntity);
        expected.setStatus(DocumentStatus.IN_PROCESS);
        
        DocumentDto processed = documentService.processDocument(1L);
        
        verify(messageRequestService, times(1)).addMessage(expected);
        assertThat(processed).isEqualTo(expected);
    }
    
    @Test
    @DisplayName("Тестируется IllegalDocumentStatusException " +
            "при вызове processDocument(ID) для документа со статусам не NEW")
    public void testProcessDocumentThenIllegalDocumentStatusExceptionWhenStatusIsNotNew() {
        Document documentEntity = createDocumentEntity();
        documentEntity.setStatus(DocumentStatus.IN_PROCESS);
        when(documentRepository.findById(any())).thenReturn(Optional.of(documentEntity));
        
        assertThrows(IllegalDocumentStatusException.class, () -> documentService.processDocument(1L));
    }
    
    @Test
    @DisplayName("Тестируется DocumentNotFoundException " +
            "при вызове processDocument(ID) когда документ с ID не существует")
    public void testProcessDocumentThenDocumentNotFoundExceptionWhenIdNonExists() {
        when(documentRepository.findById(any())).thenReturn(Optional.empty());
        
        assertThrows(DocumentNotFoundException.class, () -> documentService.processDocument(1L));
    }
    
    @Test
    @DisplayName("Тестируется успешного обновление статуса документа " +
            "при вызове updateStatus(ID, DocumentStatus.ACCEPTED)")
    public void testUpdateStatusToAcceptedSuccess() {
        Document documentEntity = createDocumentEntity();
        documentEntity.setStatus(DocumentStatus.IN_PROCESS);
        when(documentRepository.findById(any())).thenReturn(Optional.of(documentEntity));
        DocumentDto expected = mapper.toDocumentDto(documentEntity);
        expected.setStatus(DocumentStatus.ACCEPTED);
        
        DocumentDto updated = documentService.updateStatus(1L, DocumentStatus.ACCEPTED);
        
        assertThat(updated).isEqualTo(expected);
    }

    @Test
    @DisplayName("Тестируется успешное обновление статуса документа при вызове " +
            "updateStatus(ID, DocumentStatus.DECLINED)")
    public void testUpdateStatusToDeclinedSuccess() {
        Document documentEntity = createDocumentEntity();
        documentEntity.setStatus(DocumentStatus.IN_PROCESS);
        when(documentRepository.findById(any())).thenReturn(Optional.of(documentEntity));
        DocumentDto expected = mapper.toDocumentDto(documentEntity);
        expected.setStatus(DocumentStatus.DECLINED);
        
        DocumentDto updated = documentService.updateStatus(1L, DocumentStatus.DECLINED);
        
        assertThat(updated).isEqualTo(expected);
    }
    
    @Test
    @DisplayName("Тестируется DocumentNotFoundException при вызове " +
            "updateStatus(ID, DocumentStatus) когда документ с ID не существует")
    public void testUpdateStatusThenDocumentNotFoundExceptionWhenIdNonExists() {
        when(documentRepository.findById(any())).thenReturn(Optional.empty());
        
        assertThrows(DocumentNotFoundException.class,
                () -> documentService.updateStatus(1L, DocumentStatus.DECLINED));
        
    }
    
    @Test
    @DisplayName("Тестируется IllegalDocumentStatusException " +
            "при вызове updateStatus(ID, DocumentStatus) для документа со статусам не IN_PROCESS")
    public void testUpdateStatusThenIllegalDocumentStatusExceptionWhenStatusIsNotInProcess() {
        Document documentEntity = createDocumentEntity();
        when(documentRepository.findById(any())).thenReturn(Optional.of(documentEntity));
        
        assertThrows(IllegalDocumentStatusException.class,
                () -> documentService.updateStatus(1L, DocumentStatus.DECLINED));
    }
    
    /**
     * Возвращает документ заполненный документ со статусом {@code DocumentStatus.NEW}.
     *
     * @return заполненный документ
     */
    private Document createDocumentEntity() {
        Document document = new Document();
        String value = randomAlphabetic(1);
        document.setOrganization(value);
        document.setPatient(value);
        document.setDescription(value);
        document.setType(value);
        document.setId(1L);
        document.setStatus(DocumentStatus.NEW);
        document.setDate(LocalDate.now());
        return document;
    }
    
}
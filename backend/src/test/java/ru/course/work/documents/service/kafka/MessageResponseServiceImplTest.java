package ru.course.work.documents.service.kafka;

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
import ru.course.work.documents.dto.StatusResponseDto;
import ru.course.work.documents.exceptions.DocumentNotFoundException;
import ru.course.work.documents.exceptions.IllegalDocumentStatusException;
import ru.course.work.documents.persistence.entity.MessageResponse;
import ru.course.work.documents.persistence.repository.MessageResponseRepository;
import ru.course.work.documents.service.DocumentService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Класс, тестирующий {@code MessageResponseServiceImpl}
 */
@RunWith(SpringRunner.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = {MessageResponseServiceImpl.class})
public class MessageResponseServiceImplTest {
    
    @Autowired
    private MessageResponseServiceImpl messageResponseService;
    
    @MockBean
    private MessageResponseRepository repository;
    
    @MockBean
    private DocumentService documentService;
    
    @Test
    @DisplayName("Тестируется успешная обработка сообщения при вызове processStatusResponse(StatusResponseDto)")
    public void testProcessStatusResponseSuccess() {
        StatusResponseDto responseDto = createStatusResponseDto();
        
        when(repository.existsById(any())).thenReturn(false);
        
        Optional<StatusResponseDto> processed = messageResponseService.processStatusResponse(responseDto);
        
        assertThat(processed.get()).isEqualTo(responseDto);
        verify(documentService, times(1)).updateStatus(responseDto.getDocumentId(),
                                                                            DocumentStatus.valueOf(responseDto.getStatus()));
        verify(repository, times(1)).save(new MessageResponse(responseDto.getIdempotentKey()));
    }
    
    @Test
    @DisplayName("Тестируется пропуск уже обработанного сообщения при вызове processStatusResponse(StatusResponseDto) ")
    public void testProcessStatusResponseSkipResponse() {
        StatusResponseDto responseDto = createStatusResponseDto();
        
        when(repository.existsById(any())).thenReturn(true);
        Optional<StatusResponseDto> processed = messageResponseService.processStatusResponse(responseDto);
        
        assertThat(processed).isEqualTo(Optional.empty());
        
        verify(documentService, never()).updateStatus(anyLong(), any());
        verify(repository, never()).save(any());
        
    }
    
    @Test
    @DisplayName("Тестируется IllegalDocumentStatusException при вызове " +
            "processStatusResponse(StatusResponseDto)")
    public void testProcessStatusIllegalDocumentStatusException() {
        StatusResponseDto responseDto = createStatusResponseDto();
        
        when(repository.existsById(any())).thenReturn(false);
        when(documentService.updateStatus(anyLong(), any())).thenThrow(new IllegalDocumentStatusException("status exception"));
        
        assertThrows(IllegalDocumentStatusException.class,
                () -> messageResponseService.processStatusResponse(responseDto));
    }
    
    @Test
    @DisplayName("Тестируется DocumentNotFoundException при вызове " +
            "processStatusResponse(StatusResponseDto)")
    public void testProcessStatusThenDocumentNotFoundException() {
        StatusResponseDto responseDto = createStatusResponseDto();
        
        when(repository.existsById(any())).thenReturn(false);
        when(documentService.updateStatus(anyLong(), any())).thenThrow(new DocumentNotFoundException("not found"));
        
        assertThrows(DocumentNotFoundException.class,
                () -> messageResponseService.processStatusResponse(responseDto));
    }
    
    /**
     * Возвращает заполненный {@code StatusResponseDto}.
     *
     * @return возвращает заполненный {@code StatusResponseDto}
     */
    private StatusResponseDto createStatusResponseDto() {
        StatusResponseDto responseDto = new StatusResponseDto();
        responseDto.setIdempotentKey(1L);
        responseDto.setStatus("ACCEPTED");
        responseDto.setDocumentId(1L);
        return responseDto;
    }
    
}
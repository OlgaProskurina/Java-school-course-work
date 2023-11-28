package ru.course.work.documents.service.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.SettableListenableFuture;
import ru.course.work.documents.DocumentStatus;
import ru.course.work.documents.dto.DocumentDto;
import ru.course.work.documents.persistence.entity.MessageRequest;
import ru.course.work.documents.persistence.repository.MessageRequestRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Класс, тестирующий {@code MessageRequestServiceImpl}
 */
@RunWith(SpringRunner.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = {ObjectMapper.class, MessageRequestServiceImpl.class})
public class MessageRequestServiceImplTest {
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MessageRequestServiceImpl messageRequestService;
    
    @MockBean
    MessageRequestRepository repository;
    
    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Test
    @DisplayName("Тестируется успешный вызов processMessages() когда сообщение существует")
    public void testProcessMessagesSuccessWhenMessageExists() {
        DocumentDto documentDto = createDocumentDto();
        MessageRequest messageRequest = createMessageRequest(documentDto);
        ProducerRecord<String, Object> expected = new ProducerRecord<>("topic", documentDto);
        
        SettableListenableFuture<SendResult<String, Object>> future = new SettableListenableFuture<>();
        future.set(new SendResult<>(expected, null));
        
        when(repository.findUnsentMessage()).thenReturn(Optional.of(messageRequest));
        when(kafkaTemplate.send(any(), any())).thenReturn(future);
        
        Optional<ProducerRecord<String, Object>> result = messageRequestService.processMessages();
        
        assertThat(result.get()).isEqualTo(expected);
    }
    
    @Test
    @DisplayName("Тестируется успешный вызов processMessages() когда сообщение не существует")
    public void testProcessMessagesSuccessWhenMessageNonExists() {
        when(repository.findUnsentMessage()).thenReturn(Optional.empty());
        
        Optional<ProducerRecord<String, Object>> result = messageRequestService.processMessages();
        
        assertThat(result).isEqualTo(Optional.empty());
    }
    
    /**
     * Возвращает заполненный {@code DocumentDto} со статусом {@code DocumentStatus.IN_PROCESS}
     *
     * @return возвращает заполненный {@code DocumentDto} со статусом {@code DocumentStatus.IN_PROCESS
     */
    private DocumentDto createDocumentDto() {
        DocumentDto documentDto = new DocumentDto();
        var value = randomAlphabetic(1);
        documentDto.setOrganization(value);
        documentDto.setPatient(value);
        documentDto.setDescription(value);
        documentDto.setType(value);
        documentDto.setDate(LocalDate.now());
        documentDto.setStatus(DocumentStatus.IN_PROCESS);
        return documentDto;
    }
    
    /**
     * Возвращает заполненный {@code MessageRequest} с {@code documentDto}.
     *
     * @param documentDto документ
     * @return возвращает заполненный {@code MessageRequest} с {@code documentDto}
     */
    private MessageRequest createMessageRequest(DocumentDto documentDto) {
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setId(1L);
        messageRequest.setPayload(objectMapper.convertValue(documentDto, JsonNode.class));
        return messageRequest;
    }
    
}
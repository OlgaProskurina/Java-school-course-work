package ru.course.work.documents.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.course.work.documents.dto.DocumentDto;
import ru.course.work.documents.dto.IdDto;
import ru.course.work.documents.dto.IdsDto;
import ru.course.work.documents.exceptions.DocumentNotFoundException;
import ru.course.work.documents.exceptions.IllegalDocumentStatusException;
import ru.course.work.documents.service.DocumentServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Класс тестирующий методы контроллера {@link ru.course.work.documents.controller.DocumentController}
 */
@RunWith(SpringRunner.class)
@WebMvcTest
public class DocumentControllerTest {
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private DocumentServiceImpl documentService;
    
    private static final String BASE_PATH = "/documents";
    private static final String SEND_PATH = BASE_PATH + "/send";
    
    @Test
    @DisplayName("Тестируется статус 200 и возвращение DocumentDto при вызове save(DocumentDto)")
    public void testSaveThenStatusIs200AndDocumentDtoReturned() throws Exception {
        DocumentDto documentDto = createDocumentDtoForSave();
        
        when(documentService.save(any())).thenReturn(documentDto);
        
        mockMvc.perform(actionPost(BASE_PATH, documentDto))
                        .andExpect(status().isOk())
                        .andExpect(content().json(objectMapper.writeValueAsString(documentDto)));
    }
    
    @Test
    @DisplayName("Тестируется статус 200 и возвращение List<DocumentDto> при вызове get()")
    public void testGetThenStatusIs200AndDocumentDtoListReturned() throws Exception {
        List<DocumentDto> documentDtoList = new ArrayList<>();
        DocumentDto documentDto = createDocumentDtoForSave();
        documentDtoList.add(documentDto);
        
        when(documentService.findAll()).thenReturn(documentDtoList);
        
        mockMvc.perform(get(BASE_PATH))
                       .andExpect(status().isOk())
                       .andExpect(content().json(objectMapper.writeValueAsString(documentDtoList)));
    }
    
    @Test
    @DisplayName("Тестируется статус 200 и возвращение DocumentDto при вызове send(IdDto)")
    public void testSendThenStatusIs200AndDocumentDtoReturned() throws Exception {
        DocumentDto sentDocument = createDocumentDtoForSave();
        when(documentService.processDocument(any())).thenReturn(sentDocument);
        
        IdDto idDto = createIdDto();
        
        mockMvc.perform(actionPost(SEND_PATH, idDto))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(sentDocument)));
    }
    
    @Test
    @DisplayName("Тестируется статус 400 и DocumentNotFoundException при вызове send(IdDto) для несуществующего документа")
    public void testSendWhenDocumentDoesNotExistsThenStatusIs400() throws Exception {
        when(documentService.processDocument(any())).thenThrow(new DocumentNotFoundException("Документа не существует"));
        
        IdDto idDto = createIdDto();
        
        mockMvc.perform(actionPost(SEND_PATH, idDto))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                                                instanceof DocumentNotFoundException));
    }
    
    @Test
    @DisplayName("Тестируется статус 400 и IllegalDocumentStatusException при вызове send(IdDto) для документа не в статусе IN_PROCESS")
    public void testSendWhenDocumentStatusNotEqualsInProcessThenStatusIs400() throws Exception {
        when(documentService.processDocument(any())).thenThrow(new IllegalDocumentStatusException("Статус не IN_PROCESS"));
        
        IdDto idDto = createIdDto();
       
        mockMvc.perform(actionPost(SEND_PATH, idDto))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                                                instanceof IllegalDocumentStatusException));
    }
    
    @Test
    @DisplayName("Тестируется статус 200 при вызове deleteAll(IdsDto)")
    public void testDeleteAllThenStatusIs200() throws Exception {
        IdsDto idsDto = new IdsDto();
        idsDto.setIds(Set.of(1L));
        
        mockMvc.perform(delete(BASE_PATH, idsDto)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(idsDto)))
                        .andExpect(status().isOk());
    }
    
    
    /**
     * Возвращает {@code DocumentDto} заполненное данными, необходимыми для сохранения документа.
     *
     * @return дто документа
     */
    private DocumentDto createDocumentDtoForSave() {
        DocumentDto documentDto = new DocumentDto();
        var value = randomAlphabetic(1);
        
        documentDto.setOrganization(value);
        documentDto.setPatient(value);
        documentDto.setDescription(value);
        documentDto.setType(value);
        
        return documentDto;
    }
    
    /**
     * Возвращает post с установленными contentType в APPLICATION_JSON и content.
     *
     * @param uri путь
     * @param content дто
     * @return post с установленными contentType в APPLICATION_JSON и content
     */
    private MockHttpServletRequestBuilder actionPost(String uri, Object content) throws JsonProcessingException {
        return post(uri)
               .contentType(APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(content));
    }
    
    /**
     * Создает и возвращает заполненный {@code IdDto}.
     *
     * @return заполненный {@code IdDto}
     */
    private static IdDto createIdDto() {
        IdDto idDto = new IdDto();
        idDto.setId(1L);
        return idDto;
    }
}
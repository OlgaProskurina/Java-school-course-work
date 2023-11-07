package ru.template.example.documents.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.template.example.documents.dto.DocumentDto;
import ru.template.example.documents.dto.IdDto;
import ru.template.example.documents.dto.IdsDto;
import ru.template.example.documents.service.DocumentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {
    
    /**
     * Сервис по работе с документами.
     */
    private final DocumentService documentService;
    
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentDto save(@Valid @RequestBody DocumentDto dto) {
        return documentService.save(dto);
    }
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DocumentDto> get() {
        return documentService.findAll();
    }
    
    @PostMapping(
            path = "send",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentDto send(@Valid @RequestBody IdDto id) {
       return documentService.processDocument(id.getId());
    }
    
    @DeleteMapping(path = "/{id}")
    public void delete(@Valid @PathVariable Long id) {
        documentService.delete(id);
    }
    
    @DeleteMapping
    public void deleteAll(@Valid @RequestBody IdsDto idsDto) {
        documentService.deleteAll(idsDto.getIds());
    }
    
}

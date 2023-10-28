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
import ru.template.example.documents.controller.dto.DocumentDto;
import ru.template.example.documents.controller.dto.IdDto;
import ru.template.example.documents.controller.dto.IdsDto;
import ru.template.example.documents.entity.Document;
import ru.template.example.documents.service.DocumentService;
import ru.template.example.utils.DocumentMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {
    
    private final DocumentService documentService;
    
    private final DocumentMapper documentMapper;
    
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentDto save(@RequestBody DocumentDto dto) {
        Document document = documentMapper.toDocument(dto);
        documentService.save(document);
        return documentMapper.toDocumentDto(document);
    }
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DocumentDto> get() {
        return documentService.findAll()
                .stream()
                .map(documentMapper::toDocumentDto)
                .collect(Collectors.toList());
    }
    
    @PostMapping(
            path = "send",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentDto send(@RequestBody IdDto id) {
        Document document = documentService.processDocument(id.getId());
        return documentMapper.toDocumentDto(document);
    }
    
    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        documentService.delete(id);
    }
    
    @DeleteMapping
    public void deleteAll(@RequestBody IdsDto idsDto) {
        documentService.deleteAll(idsDto.getIds());
    }
    
}

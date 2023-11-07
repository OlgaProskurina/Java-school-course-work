package ru.template.example.documents.dto;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class IdDto {
    
    @NotNull
    private Long id;
    
}

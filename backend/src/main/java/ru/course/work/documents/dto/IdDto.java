package ru.course.work.documents.dto;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class IdDto {
    
    @NotNull
    private Long id;
    
}

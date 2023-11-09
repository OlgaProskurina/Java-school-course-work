package ru.course.work.documents.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
public class IdsDto {

    @NotEmpty
    private Set<Long> ids;

}

package ru.template.example.documents;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DocumentStatus {
    
    NEW("NEW", "Новый"),
    IN_PROCESS("IN_PROCESS", "В обработке"),
    DECLINED("DECLINED", "Отклонен"),
    ACCEPTED("ACCEPTED", "Принят");
    
    private final String code;
    private final String name;

}

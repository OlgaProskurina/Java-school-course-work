package ru.course.work.documents;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DocumentStatus {
    
    NEW("NEW") {
        @Override
        public String getName() {
            return "Новый";
        }
    }, IN_PROCESS("IN_PROCESS") {
        @Override
        public String getName() {
            return "В обработке";
        }
    }, DECLINED("DECLINED") {
        @Override
        @JsonProperty("name")
        public String getName() {
            return "Отклонен";
        }
    }, ACCEPTED("ACCEPTED") {
        @Override
        public String getName() {
            return "Принят";
        }
    };
    private final String code;
    
    @JsonProperty("name")
    public String getName() {
        return "Неизвестно";
    }
}
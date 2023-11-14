package ru.template.example.documents.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ДТО для DLQ топика.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DlqMessageResponseDto {
    
    /**
     * Сообщение об ошибке.
     */
    private String errorMessage;
    /**
     * То что привело к ошибке.
     */
    private StatusResponseDto statusResponse;

}
package ru.template.example.documents.expeptions;

/**
 * Неверный статус документа.
 */
public class IllegalDocumentStatusException extends RuntimeException {
    public IllegalDocumentStatusException(String message) {
        super(message);
    }
}
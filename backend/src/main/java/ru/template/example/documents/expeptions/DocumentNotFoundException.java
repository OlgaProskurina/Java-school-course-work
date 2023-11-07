package ru.template.example.documents.expeptions;

/**
 * Документ не найден.
 */
public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(String message) {
        super(message);
    }
}

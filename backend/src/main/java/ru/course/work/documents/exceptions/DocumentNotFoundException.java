package ru.course.work.documents.exceptions;

/**
 * Документ не найден.
 */
public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(String message) {
        super(message);
    }
}

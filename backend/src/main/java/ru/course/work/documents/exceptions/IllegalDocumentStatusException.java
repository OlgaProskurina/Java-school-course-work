package ru.course.work.documents.exceptions;

/**
 * Неверный статус документа.
 */
public class IllegalDocumentStatusException extends RuntimeException {
    public IllegalDocumentStatusException(String message) {
        super(message);
    }
}
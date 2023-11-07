package ru.template.example.documents.messaging.exception;

/**
 * Сообщение из брокера не валидно.
 */
public class InvalidPayloadException extends RuntimeException {
    public InvalidPayloadException(String message) {
        super(message);
    }
}
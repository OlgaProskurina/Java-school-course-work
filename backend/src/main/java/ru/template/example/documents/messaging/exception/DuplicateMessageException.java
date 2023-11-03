package ru.template.example.documents.messaging.exception;

public class DuplicateMessageException extends Exception {
    public DuplicateMessageException(String message) {
        super(message);
    }
}

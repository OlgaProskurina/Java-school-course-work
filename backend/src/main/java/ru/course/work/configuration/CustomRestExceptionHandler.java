package ru.course.work.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.course.work.documents.exceptions.DocumentNotFoundException;
import ru.course.work.documents.exceptions.IllegalDocumentStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Обработчик исключений контроллера.
 */
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
    
    /**
     * 400
     */
    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        logger.error("BAD REQUEST: Validation error", ex);
        
        List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        RestApiError restApiError = new RestApiError("Validation failed", errors);
        return handleExceptionInternal(ex, restApiError, headers, BAD_REQUEST, request);
    }
    
    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        logger.error("BAD REQUEST: Http message is not readable", ex);
        List<String> errors = List.of(ex.getRootCause() == null ? ex.getMessage() : ex.getRootCause().getMessage());
        RestApiError restApiError = new RestApiError("Http message is not readable", errors);
        return handleExceptionInternal(ex, restApiError, headers, BAD_REQUEST, request);
    }
    
    /**
     * Обработчик {@code IllegalDocumentStatusException}
     */
    @ExceptionHandler({IllegalDocumentStatusException.class})
    public ResponseEntity<RestApiError> handleIllegalDocumentStateException(final IllegalDocumentStatusException ex,
                                                                            final WebRequest request) {
        logger.error("BAD REQUEST: Illegal status exception", ex);
        RestApiError restApiError = new RestApiError("Illegal status", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity<>(restApiError, new HttpHeaders(), BAD_REQUEST);
    }
    
    /**
     * Обработчик {@code DocumentNotFoundException}
     */
    @ExceptionHandler({DocumentNotFoundException.class})
    public ResponseEntity<RestApiError> handleDocumentNotFoundException(final DocumentNotFoundException ex,
                                                                        final WebRequest request) {
        logger.error("BAD REQUEST: Document not found exception", ex);
        RestApiError restApiError = new RestApiError("Document not found", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity<>(restApiError, new HttpHeaders(), BAD_REQUEST);
    }
    
    /**
     * 500
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<RestApiError> handleAll(final Exception ex, final WebRequest request) {
        logger.error("INTERNAL SERVER ERROR: Internal server error", ex);
        RestApiError restApiError = new RestApiError("Internal server error", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity<>(restApiError, new HttpHeaders(), INTERNAL_SERVER_ERROR);
    }
    
    @Setter
    @Getter
    public static class RestApiError {
        
        private String message;
        
        private List<String> errors;
        
        public RestApiError(String message, List<String> errors) {
            this.message = message;
            this.errors = errors;
        }
        
    }
}
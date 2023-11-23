package ru.course.work.documents.validation;

import ru.course.work.documents.DocumentStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Валидатор для статуса из результата обработки документа, где статус валидный, если равен
 * или {@code ACCEPTED} или {@code DECLINED}.
 */
public class StatusResponseValidator implements ConstraintValidator<StatusResponseConstraint, String> {
    @Override
    public void initialize(StatusResponseConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
    
    @Override
    public boolean isValid(String statusCode, ConstraintValidatorContext context) {
        return DocumentStatus.ACCEPTED.getCode().equals(statusCode) || DocumentStatus.DECLINED.getCode().equals(statusCode);
    }
}
package ru.course.work.documents.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ограничение на поле со статусом, где он может быть или {@code ACCEPTED} или {@code DECLINED}.
 */
@Documented
@Constraint(validatedBy = StatusResponseValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StatusResponseConstraint {
    String message() default "Status should be \"ACCEPTED\" or \"DECLINED\"";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
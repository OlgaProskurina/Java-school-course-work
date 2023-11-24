package ru.course.work.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.course.work.documents.controller.DocumentController;

/**
 * Класс для логирования вызовов методов контроллера {@link DocumentController}.
 */
@Component
@Aspect
@Slf4j
public class ControllersLogging {
    
    /**
     * Перехват вызова всех публичных методов.
     */
    @Pointcut("execution(public * ru.course.work.documents.controller.DocumentController.*(..))")
    public void pointcut() {}

    /**
     * Логирование вызова метода и его аргументов.
     */
    @Before("pointcut()")
    public void logInfoMethodCall(JoinPoint joinPoint) {
        log.info("Call controller method {} args: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

}
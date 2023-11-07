package ru.template.example.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Класс для логирования вызовов методов контроллера {@link ru.template.example.documents.controller.DocumentController}.
 */
@Component
@Aspect
@Slf4j
public class ControllersLogging {
    
    /**
     * Перехват вызова всех публичных методов.
     */
    @Pointcut("execution(public * ru.template.example.documents.controller.DocumentController.*(..))")
    public void pointcut() {}

    /**
     * Логирование вызова метода и его аргументов.
     */
    @Before("pointcut()")
    public void logInfoMethodCall(JoinPoint joinPoint) {
        log.info("Вызов метода контроллера {}, args: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

}
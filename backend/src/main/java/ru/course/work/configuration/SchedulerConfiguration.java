package ru.course.work.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(value = "outbox.scheduler.enable", havingValue = "true", matchIfMissing = false)
public class SchedulerConfiguration {

}

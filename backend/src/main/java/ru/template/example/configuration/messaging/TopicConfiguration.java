package ru.template.example.configuration.messaging;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TopicConfiguration {
    
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    
    @Value(value = "kafka.topic.response-document")
    private String responseTopic;
    @Value(value = "kafka.topic.process_document")
    private String processTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }
    
    @Bean
    public NewTopic processProcessDocumentTopic() {
        return new NewTopic(processTopic, 1, (short) 1);
    }
    
    @Bean
    public NewTopic responseDocumentStateTopic() {
        return new NewTopic(responseTopic, 1, (short) 1);
    }
}

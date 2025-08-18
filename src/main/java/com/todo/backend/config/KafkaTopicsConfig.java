package com.todo.backend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicsConfig {

    @Value("${app.kafka.topic:todo-topic}")
    private String topic;

    // 파티션/복제계수 단일 노드 기본값
    @Bean
    public NewTopic todoTopic() {
        return new NewTopic(topic, 1, (short) 1);
    }
}

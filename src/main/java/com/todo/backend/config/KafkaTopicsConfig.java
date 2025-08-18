// src/main/java/com/todo/backend/config/KafkaTopicsConfig.java
package com.todo.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Profile;

@Profile("!test")   // ← 테스트 프로필에선 토픽 자동생성 제외
@Configuration
public class KafkaTopicsConfig {
    @Value("${app.kafka.topic:todo-topic}")
    private String topic;

    @Bean
    public NewTopic todoTopic() {
        return new NewTopic(topic, 1, (short) 1);
    }
}

package com.todo.backend.service;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.topic:todo-topic}")
    private String topic;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /** 비동기 발행 */
    public void send(String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Kafka send failed: {}", ex.getMessage(), ex);
            } else if (result != null) {
                RecordMetadata m = result.getRecordMetadata();
                log.info("Kafka sent OK topic={} partition={} offset={} value={}",
                        m.topic(), m.partition(), m.offset(), message);
            }
        });
    }

    /** 동기 결과 대기 (타임아웃 포함) */
    public String sendAndAwait(String message) {
        try {
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
            SendResult<String, String> result = future.get(10, TimeUnit.SECONDS); // 필요시 타임아웃 조절
            RecordMetadata m = result.getRecordMetadata();
            String info = String.format("OK topic=%s partition=%d offset=%d",
                    m.topic(), m.partition(), m.offset());
            log.info("Kafka sent (await) {}", info);
            return info;
        } catch (Exception e) {
            log.error("Kafka send error", e);
            return "ERROR: " + e.getMessage();
        }
    }
}

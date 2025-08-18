package com.todo.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class ConsumerListener {

    private static final Logger log = LoggerFactory.getLogger(ConsumerListener.class);

    @KafkaListener(
            topics = "${app.kafka.topic:todo-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String value,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                       @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Kafka consumed topic={} partition={} offset={} value={}",
                topic, partition, offset, value);
        // TODO: 여기서 실제 처리 로직 수행
    }
}

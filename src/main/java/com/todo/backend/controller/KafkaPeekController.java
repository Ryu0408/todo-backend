package com.todo.backend.controller;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/kafka")
public class KafkaPeekController {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrap;

    @Value("${app.kafka.topic:todo-topic}")
    private String topic;

    public record PeekRecord(int partition, long offset, long timestamp, String key, String value) {}

    /**
     * 최근 N개 메시지 조회 (기본 10개)
     * 예) GET /kafka/tail?limit=10
     * 컨텍스트 경로가 /api면 /api/kafka/tail
     */
    @GetMapping("/tail")
    public ResponseEntity<List<PeekRecord>> tail(@RequestParam(defaultValue = "10") int limit) {
        if (limit <= 0) limit = 10;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "peek-" + UUID.randomUUID()); // 매 요청 임시 그룹
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");            // 커밋 안 함
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");            // 기본 latest

        List<PeekRecord> out = new ArrayList<>();
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            // 파티션 정보 확보 후 직접 assign
            List<TopicPartition> partitions = consumer.partitionsFor(topic).stream()
                    .map(pi -> new TopicPartition(pi.topic(), pi.partition()))
                    .collect(Collectors.toList());
            if (partitions.isEmpty()) return ResponseEntity.ok(List.of());

            consumer.assign(partitions);

            // 각 파티션의 end/begin offset 조회
            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);
            Map<TopicPartition, Long> beginOffsets = consumer.beginningOffsets(partitions);

            // 각 파티션별로 끝에서 limit 만큼 뒤로 이동(음수 방지)
            for (TopicPartition tp : partitions) {
                long end = endOffsets.getOrDefault(tp, 0L);
                long begin = beginOffsets.getOrDefault(tp, 0L);
                long start = Math.max(begin, end - limit); // 단순히 각 파티션에서 limit만큼
                consumer.seek(tp, start);
            }

            long deadline = System.currentTimeMillis() + 2000; // 최대 2초 폴링
            while (System.currentTimeMillis() < deadline && out.size() < limit) {
                var records = consumer.poll(Duration.ofMillis(200));
                for (ConsumerRecord<String, String> r : records) {
                    out.add(new PeekRecord(r.partition(), r.offset(), r.timestamp(), r.key(), r.value()));
                    if (out.size() >= limit) break;
                }
            }

            // 정렬: offset 오름차순
            out.sort(Comparator.comparingLong(PeekRecord::offset));
            return ResponseEntity.ok(out);
        }
    }
}

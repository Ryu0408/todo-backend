package com.todo.backend.controller;

import com.todo.backend.service.KafkaProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka")
public class KafkaTestController {

    private final KafkaProducerService producer;

    public KafkaTestController(KafkaProducerService producer) {
        this.producer = producer;
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    // 예: POST /api/kafka/publish?msg=hello  (context-path=/api 고려)
    @PostMapping("/publish")
    public ResponseEntity<String> publish(@RequestParam("msg") String msg) {
        producer.send(msg);
        return ResponseEntity.ok("sent: " + msg);
    }

    // 예: POST /api/kafka/publish/json  body: {"message":"hello"}
    @PostMapping("/publish/json")
    public ResponseEntity<String> publishJson(@RequestBody PublishRequest req) {
        producer.send(req.message());
        return ResponseEntity.ok("sent: " + req.message());
    }

    // 예: POST /api/kafka/publish/await?msg=hello
    @PostMapping("/publish/await")
    public ResponseEntity<String> publishAwait(@RequestParam("msg") String msg) {
        return ResponseEntity.ok(producer.sendAndAwait(msg));
    }

    public record PublishRequest(String message) {}
}

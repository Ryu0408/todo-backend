// src/main/java/com/todo/backend/service/FilePublishService.java
package com.todo.backend.service;

import com.todo.backend.dto.FileMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FilePublishService {
    private final RabbitTemplate rabbitTemplate;

    public void publish(FileMessage message) {
        rabbitTemplate.convertAndSend("file.exchange", "file.routing", message);
    }
}

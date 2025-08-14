package com.todo.backend.service;

import com.todo.backend.dto.FileMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FilePublishService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbit.exchange:file.exchange}")
    private String exchange;

    @Value("${app.rabbit.routing-key:file.routing}")
    private String routingKey;

    public FilePublishService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(FileMessage message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}

package com.todo.backend.service;

import com.todo.backend.dto.FileMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileConsumer {

    private static final Logger log = LoggerFactory.getLogger(FileConsumer.class);

    @Value("${app.rabbit.queue:file.queue}")
    private String queueName; // 단순 표기용(필수는 아님)

    @RabbitListener(queues = "${app.rabbit.queue:file.queue}")
    public void handle(FileMessage msg) throws InterruptedException {
        log.info("[consumer] queue={} received: {}", queueName, msg);
        // TODO: 실제 처리 (S3 업로드/DB기록/썸네일 등)
        Thread.sleep(200);
        log.info("[consumer] processed uploadId={} s3Key={}", msg.uploadId(), msg.s3Key());
    }
}

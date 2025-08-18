// src/main/java/com/todo/backend/service/FileConsumer.java
package com.todo.backend.service;

import com.todo.backend.dto.FileMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileConsumer {

    private final FileUploadService fileUploadService;
    private final S3Service s3;

    @RabbitListener(queues = "${app.rabbit.queue:file.queue}")
    public void handle(FileMessage msg) {
        final String id = msg.uploadId();
        final Path path = Path.of(msg.localPath());

        try {
            log.info("[consumer] start id={} name={} size={} type={} path={}",
                    id, msg.originalName(), msg.size(), msg.contentType(), path);

            fileUploadService.markProcessing(id);

            // 실제 업로드
            s3.putObject(msg.s3Key(), path, msg.contentType());

            fileUploadService.markDone(id);
            log.info("[consumer] done   id={} s3Key={}", id, msg.s3Key());

        } catch (Exception e) {
            fileUploadService.markFailed(id, e.getMessage());
            log.error("[consumer] fail   id={} err={}", id, e.toString(), e);
            throw new AmqpRejectAndDontRequeueException("Processing failed for " + id, e);

        } finally {
            try { Files.deleteIfExists(path); } catch (Exception ignore) {}
        }
    }
}

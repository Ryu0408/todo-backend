package com.todo.backend.controller;

import com.todo.backend.dto.FileMessage;
import com.todo.backend.service.FilePublishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileQueueController {

    private final FilePublishService publishService;

    public FileQueueController(FilePublishService publishService) {
        this.publishService = publishService;
    }

    // 파일 메타만 큐에 적재 (실제 업로드는 Consumer에서)
    @PostMapping(path = "/queue", consumes = "multipart/form-data")
    public ResponseEntity<String> enqueue(@RequestParam("file") MultipartFile file) {
        String uploadId = UUID.randomUUID().toString();
        FileMessage msg = new FileMessage(
                uploadId,
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType(),
                "s3/pending/" + uploadId
        );
        publishService.publish(msg);
        return ResponseEntity.accepted().body("queued: " + uploadId);
    }
}

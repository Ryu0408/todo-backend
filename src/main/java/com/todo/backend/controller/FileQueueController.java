package com.todo.backend.controller;

import com.todo.backend.dto.FileMessage;
import com.todo.backend.service.FilePublishService;
import com.todo.backend.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileQueueController {

    private final FilePublishService publishService;
    private final FileUploadService fileUploadService;

    public record EnqueueResponse(
            String uploadId, String status,
            String originalName, long size, String contentType, String s3Key
    ) {}

    @PostMapping(path="/queue", consumes=MediaType.MULTIPART_FORM_DATA_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EnqueueResponse> enqueue(@RequestPart("file") MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String uploadId = UUID.randomUUID().toString();
        String contentType = file.getContentType() != null ? file.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String s3Key = "s3/pending/" + uploadId;

        // 1) 임시 저장
        String originalName = file.getOriginalFilename();
        String suffix = (originalName != null && originalName.contains(".")) ? originalName.substring(originalName.lastIndexOf('.')) : ".bin";
        Path tmp = Files.createTempFile("upload-" + uploadId + "-", suffix);
        file.transferTo(tmp.toFile());

        // 2) 상태 DB에 PENDING
        fileUploadService.createPending(uploadId, originalName, contentType, file.getSize(), s3Key);

        // 3) 큐 발행(localPath 포함)
        publishService.publish(new FileMessage(
                uploadId, originalName, file.getSize(), contentType, s3Key, tmp.toString()
        ));

        log.info("[api] queued id={} name={} size={} type={} tmp={}", uploadId, originalName, file.getSize(), contentType, tmp);

        return ResponseEntity.accepted()
                .location(URI.create("/files/%s/status".formatted(uploadId)))
                .body(new EnqueueResponse(uploadId, "PENDING", originalName, file.getSize(), contentType, s3Key));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> status(@PathVariable String id){
        var row = fileUploadService.get(id);
        if (row == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(java.util.Map.of(
                "uploadId", row.getUploadId(),
                "status",   row.getStatus(),
                "s3Key",    row.getS3Key(),
                "error",    row.getError()
        ));
    }
}

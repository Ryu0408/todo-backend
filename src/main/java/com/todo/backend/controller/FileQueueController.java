package com.todo.backend.controller;

import com.todo.backend.dto.FileMessage;
import com.todo.backend.service.FilePublishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileQueueController {

    private final FilePublishService publishService;

    // 202 응답 바디용 DTO
    public record EnqueueResponse(
            String uploadId,
            String status,        // PENDING
            String originalName,
            long size,
            String contentType,
            String s3Key
    ) {}

    @PostMapping(
            path = "/queue",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<EnqueueResponse> enqueue(@RequestPart("file") MultipartFile file) {
        // 1) 기본 검증
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // 2) 메타 구성
        String uploadId   = UUID.randomUUID().toString();
        String contentType = file.getContentType() != null
                ? file.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String s3Key = "s3/pending/" + uploadId;

        // 3) 메시지 발행
        FileMessage msg = new FileMessage(
                uploadId,
                file.getOriginalFilename(),
                file.getSize(),
                contentType,
                s3Key
        );
        publishService.publish(msg);

        // 4) 로그 + 202 응답(JSON)
        log.info("[api] queued uploadId={} name={} size={} type={}",
                uploadId, file.getOriginalFilename(), file.getSize(), contentType);

        EnqueueResponse body = new EnqueueResponse(
                uploadId, "PENDING",
                file.getOriginalFilename(), file.getSize(), contentType, s3Key
        );

        // (선택) Location 헤더: 추후 상태조회 엔드포인트(/files/{id}/status)를 만들면 자연스럽게 연결됨
        return ResponseEntity.accepted()
                .location(URI.create("/files/%s/status".formatted(uploadId)))
                .body(body);
    }
}

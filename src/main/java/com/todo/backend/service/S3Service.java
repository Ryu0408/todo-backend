// src/main/java/com/todo/backend/service/S3Service.java
package com.todo.backend.service;

import com.todo.backend.config.AwsS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.nio.file.Files;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AwsS3Properties props;
    private final S3Client s3;
    private final S3Presigner presigner;

    public String uploadFile(MultipartFile file) throws java.io.IOException {
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        String key = "uploads/" + UUID.randomUUID();  // 또는 "s3/pending/"+UUID...

        // 임시 저장
        String original = file.getOriginalFilename();
        String suffix = (original != null && original.contains(".")) ? original.substring(original.lastIndexOf('.')) : ".bin";
        Path tmp = Files.createTempFile("upload-", suffix);
        file.transferTo(tmp.toFile());

        try {
            // 기존 putObject(Path, ...)
            this.putObject(key, tmp, contentType);
        } finally {
            try { Files.deleteIfExists(tmp); } catch (Exception ignore) {}
        }

        // 업로드 URL 반환(정적 퍼블릭 버킷이 아니라면 presigned URL을 쓰세요)
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                props.getBucketName(), props.getRegion(), key);
    }

    public void putObject(String key, Path path, String contentType){
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(props.getBucketName())
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromFile(path)
        );
    }

    public void putObject(String key, InputStream is, long size, String contentType){
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(props.getBucketName())
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromInputStream(is, size)
        );
    }

    public String presignedGetUrl(String key, Duration ttl){
        var req = GetObjectPresignRequest.builder()
                .signatureDuration(ttl)
                .getObjectRequest(software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
                        .bucket(props.getBucketName())
                        .key(key)
                        .build())
                .build();
        return presigner.presignGetObject(req).url().toString();
    }
}

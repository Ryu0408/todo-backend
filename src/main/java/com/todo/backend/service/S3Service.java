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

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AwsS3Properties props;
    private final S3Client s3;
    private final S3Presigner presigner;

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

package com.todo.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class S3Service {

    @Value("${aws.s3.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.s3.secretAccessKey}")
    private String secretAccessKey;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    // 파일 업로드 메서드
    public String uploadFile(MultipartFile file) throws IOException {
        // 업로드 로직 (AWS S3 관련)
        // 예시로만 작성된 로직입니다.
        File tempFile = new File(file.getOriginalFilename());
        file.transferTo(tempFile);
        // AWS S3 업로드 로직 구현...
        return "File uploaded to S3 successfully!";
    }
}

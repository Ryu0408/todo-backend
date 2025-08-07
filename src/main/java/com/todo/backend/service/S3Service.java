package com.todo.backend.service;

import com.todo.backend.config.AwsS3Properties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class S3Service {

    private final AwsS3Properties awsS3Properties;
    private final S3Client s3Client;

    public S3Service(AwsS3Properties awsS3Properties) {
        this.awsS3Properties = awsS3Properties;

        // AWS SDK 클라이언트 설정
        this.s3Client = S3Client.builder()
                .region(Region.of(awsS3Properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        awsS3Properties.getAccessKeyId(),
                        awsS3Properties.getSecretAccessKey())))
                .build();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // 파일을 임시로 저장
        Path tempFilePath = Paths.get(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
        file.transferTo(tempFilePath.toFile());

        // S3에 업로드할 객체 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsS3Properties.getBucketName())
                .key(file.getOriginalFilename()) // 객체 키 설정
                .contentType(file.getContentType())
                .build();

        // S3에 파일 업로드
        s3Client.putObject(putObjectRequest, tempFilePath);

        // 임시 파일 삭제
        Files.delete(tempFilePath);

        // S3에 업로드된 파일 URL 반환
        return String.format("https://%s.s3.%s.amazonaws.com/%s", awsS3Properties.getBucketName(), awsS3Properties.getRegion(), file.getOriginalFilename());
    }
}

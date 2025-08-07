package com.todo.backend.service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;

    // application.yml에서 AWS 관련 설정을 가져옵니다.
    public S3Service(@Value("${aws.s3.accessKeyId}") String accessKeyId,
                     @Value("${aws.s3.secretAccessKey}") String secretAccessKey,
                     @Value("${aws.s3.region}") String region,
                     @Value("${aws.s3.bucketName}") String bucketName) {

        // AWS 자격 증명 설정 (AWS SDK 2.x 방식)
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        // S3Client 인스턴스 생성 (AWS SDK 2.x)
        this.s3Client = S3Client.builder()
                .region(Region.of(region))  // Region 설정
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))  // 자격 증명 설정
                .build();

        this.bucketName = bucketName;
    }

    // 파일을 S3에 업로드하는 메서드
    public String uploadFile(MultipartFile file) throws IOException {
        // 파일을 임시로 로컬에 저장
        File tempFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        file.transferTo(tempFile);

        // S3에 파일 업로드
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key("images/" + tempFile.getName())
                .build();

        // 파일 업로드
        s3Client.putObject(putObjectRequest, tempFile.toPath());

        // 업로드된 파일 URL 반환
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key("images/" + tempFile.getName())).toString();
    }
}
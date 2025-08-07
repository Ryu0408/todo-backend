package com.todo.backend.service;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class S3Service {

    private final String accessKeyId;
    private final String secretAccessKey;
    private final String region;
    private final String bucketName;

    public S3Service() {
        // .env 파일 로드
        Dotenv dotenv = Dotenv.load();
        this.accessKeyId = dotenv.get("AWS_ACCESS_KEY_ID");
        this.secretAccessKey = dotenv.get("AWS_SECRET_ACCESS_KEY");
        this.region = dotenv.get("AWS_REGION");
        this.bucketName = dotenv.get("AWS_S3_BUCKET");
    }

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

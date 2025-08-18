// src/main/java/com/todo/backend/config/S3Config.java
package com.todo.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client(AwsS3Properties p){
        return S3Client.builder()
                .region(Region.of(p.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(p.getAccessKeyId(), p.getSecretAccessKey())))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(AwsS3Properties p){
        return S3Presigner.builder()
                .region(Region.of(p.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(p.getAccessKeyId(), p.getSecretAccessKey())))
                .build();
    }
}

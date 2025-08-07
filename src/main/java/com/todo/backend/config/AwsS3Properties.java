package com.todo.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "aws.s3")
public class AwsS3Properties {

    // Getters and setters
    private String accessKeyId;
    private String secretAccessKey;
    private String region;
    private String bucketName;

}

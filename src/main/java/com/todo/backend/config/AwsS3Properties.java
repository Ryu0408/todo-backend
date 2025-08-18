// src/main/java/com/todo/backend/config/AwsS3Properties.java
package com.todo.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws.s3")
public class AwsS3Properties {
    private String accessKeyId;
    private String secretAccessKey;
    private String region;
    private String bucketName;
    // getter/setter
    public String getAccessKeyId(){ return accessKeyId; }
    public void setAccessKeyId(String v){ this.accessKeyId = v; }
    public String getSecretAccessKey(){ return secretAccessKey; }
    public void setSecretAccessKey(String v){ this.secretAccessKey = v; }
    public String getRegion(){ return region; }
    public void setRegion(String v){ this.region = v; }
    public String getBucketName(){ return bucketName; }
    public void setBucketName(String v){ this.bucketName = v; }
}

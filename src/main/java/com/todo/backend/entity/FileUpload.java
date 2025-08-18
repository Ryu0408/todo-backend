// src/main/java/com/todo/backend/entity/FileUpload.java
package com.todo.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="file_upload")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FileUpload {
    @Id @Column(name="upload_id", length=36)
    private String uploadId;

    private String originalName;
    private String contentType;
    private long size;
    private String s3Key;

    @Column(nullable=false)
    private String status; // PENDING, PROCESSING, DONE, FAILED

    @Lob
    private String error;
}

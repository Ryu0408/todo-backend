// src/main/java/com/todo/backend/repository/FileUploadRepository.java
package com.todo.backend.repository;

import com.todo.backend.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileUploadRepository extends JpaRepository<FileUpload, String> {}

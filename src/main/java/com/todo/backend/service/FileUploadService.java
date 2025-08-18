// src/main/java/com/todo/backend/service/FileUploadService.java
package com.todo.backend.service;

import com.todo.backend.entity.FileUpload;
import com.todo.backend.repository.FileUploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final FileUploadRepository repo;

    @Transactional
    public void createPending(String id, String name, String type, long size, String s3Key){
        if (repo.existsById(id)) return;
        repo.save(FileUpload.builder()
                .uploadId(id).originalName(name).contentType(type)
                .size(size).s3Key(s3Key).status("PENDING").build());
    }

    @Transactional public void markProcessing(String id){
        repo.findById(id).ifPresent(e -> e.setStatus("PROCESSING"));
    }
    @Transactional public void markDone(String id){
        repo.findById(id).ifPresent(e -> { e.setStatus("DONE"); e.setError(null); });
    }
    @Transactional public void markFailed(String id, String err){
        repo.findById(id).ifPresent(e -> { e.setStatus("FAILED"); e.setError(err); });
    }

    public FileUpload get(String id){ return repo.findById(id).orElse(null); }
}

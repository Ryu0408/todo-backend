package com.todo.backend.controller;

import com.todo.backend.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private S3Service s3Service;

    @PostMapping
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        // S3에 파일 업로드 후 URL 반환
        return s3Service.uploadFile(file);
    }
}

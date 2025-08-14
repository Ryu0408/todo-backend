package com.todo.backend.dto;

public record FileMessage(
        String uploadId,
        String originalName,
        long size,
        String contentType,
        String s3Key
) {}

package com.example.project.models.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String put(MultipartFile file) throws IOException;

    void delete(String filename) throws IOException;
}

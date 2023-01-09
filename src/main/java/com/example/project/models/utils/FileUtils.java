package com.example.project.models.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileUtils {
    @Value("${upload.path}")
    private static String uploadPath;

    private FileUtils() {
    }

    public static String save(MultipartFile file) throws IOException {
        String uuidFile = UUID.randomUUID().toString();
        String resultFilename = uuidFile + "-" + file.getOriginalFilename();

        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        try (InputStream is = file.getInputStream()) {
            Path filePath = uploadDir.resolve(resultFilename);
            Files.copy(is, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return resultFilename;
    }

    public static void delete(String filename) throws IOException {
        Path path = Paths.get(uploadPath).resolve(filename);
        Files.deleteIfExists(path);
    }
}

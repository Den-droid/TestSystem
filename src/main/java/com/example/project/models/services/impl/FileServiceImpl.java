package com.example.project.models.services.impl;

import com.example.project.models.enums.FileType;
import com.example.project.models.services.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    private static final String[] audioFormats = {"mp3"};
    private static final String[] videoFormats = {"mp4", "avi"};
    private static final String[] imageFormats = {"jpg", "jpeg", "png", "bmp"};

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public String put(MultipartFile file) throws IOException {
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

    @Override
    public void delete(String filename) throws IOException {
        Path path = Paths.get(uploadPath).resolve(filename);
        Files.deleteIfExists(path);
    }

    private FileType getFileType(String filename) {
        String[] parts = filename.split("\\.");
        String extension = parts[parts.length - 1];

        boolean isImage = Arrays.stream(imageFormats)
                .anyMatch(x -> x.equalsIgnoreCase(extension));
        if (isImage)
            return FileType.IMAGE;
        boolean isAudio = Arrays.stream(audioFormats)
                .anyMatch(x -> x.equalsIgnoreCase(extension));
        if (isAudio)
            return FileType.AUDIO;
        boolean isVideo = Arrays.stream(videoFormats)
                .anyMatch(x -> x.equalsIgnoreCase(extension));
        if (isVideo)
            return FileType.VIDEO;

        return null;
    }

}

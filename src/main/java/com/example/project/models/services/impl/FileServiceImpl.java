package com.example.project.models.services.impl;

import com.example.project.models.enums.FileType;
import com.example.project.models.services.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    private static final String[] audioFormats = {"mp3"};
    private static final String[] videoFormats = {"mp4", "avi"};
    private static final String[] imageFormats = {"jpg", "jpeg", "png", "bmp"};

    @Value("${upload.path}")
    private String uploadPath;
    private String dirPrefix = "classpath:/static";

    @Override
    public String put(MultipartFile file) throws IOException {
        File uploadDir = new File(dirPrefix + uploadPath);
        if (!uploadDir.exists())
            uploadDir.mkdir();

        String uuidFile = UUID.randomUUID().toString();
        String resultFilename = uuidFile + "." + file.getOriginalFilename();
        String mediaPath = uploadPath + "/" + resultFilename;
        file.transferTo(new File(mediaPath));

        return resultFilename;
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

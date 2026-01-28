package com.tqtadka.platform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageUploadService {

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Value("${cdn.base-url}")
    private String cdnBaseUrl;

    public String upload(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }

        try {
            String filename =
                    UUID.randomUUID() + "-" + file.getOriginalFilename();

            Path imageDir = Paths.get(uploadDir, "images");
            Files.createDirectories(imageDir);

            Path filePath = imageDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 🔥 return public URL
            return cdnBaseUrl + "/images/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }
}
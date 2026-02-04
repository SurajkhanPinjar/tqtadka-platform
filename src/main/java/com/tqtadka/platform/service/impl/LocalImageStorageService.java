package com.tqtadka.platform.service.impl;

import com.tqtadka.platform.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@ConditionalOnProperty(
        name = "cdn.mode",
        havingValue = "local",
        matchIfMissing = true
)
public class LocalImageStorageService implements ImageStorageService {

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Override
    public String upload(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }

        try {
            String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();

            Path imagesDir = Paths.get(uploadDir, "images");
            Files.createDirectories(imagesDir);

            Path target = imagesDir.resolve(filename);
            Files.copy(file.getInputStream(), target);

            // ✅ NO localhost, NO full domain
            return "/images/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Local image upload failed", e);
        }
    }

    @Override
    public void delete(String imageUrl) {
        // optional
    }
}
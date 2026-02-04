package com.tqtadka.platform.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageUploadService {

    private final ImageStorageService imageStorageService;

    public ImageUploadService(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    public String upload(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }

        // 🔥 Delegate to correct implementation (local / R2)
        return imageStorageService.upload(file);
    }

    public void delete(String imageUrl) {
        imageStorageService.delete(imageUrl);
    }
}
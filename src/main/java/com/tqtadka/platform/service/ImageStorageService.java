package com.tqtadka.platform.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    /**
     * Uploads image and returns PUBLIC URL
     */
    String upload(MultipartFile file);

    /**
     * Deletes image (optional for later)
     */
    void delete(String imageUrl);
}
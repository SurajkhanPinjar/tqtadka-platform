package com.tqtadka.platform.admin;

import com.tqtadka.platform.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/admin/images")
@RequiredArgsConstructor
public class AdminImageController {

    private static final long MAX_IMAGE_SIZE = 400 * 1024; // 400 KB

    private final ImageStorageService imageStorageService;

    @PostMapping("/upload")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) {

        // ❌ Empty file
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File is empty"
            );
        }

        // ❌ Size limit
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Image size must be under 400 KB"
            );
        }

        // ❌ Content type check
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only image files are allowed"
            );
        }

        // ✅ Upload
        String url = imageStorageService.upload(file);

        return Map.of("url", url);
    }
}
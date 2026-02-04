package com.tqtadka.platform.admin;

import com.tqtadka.platform.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/admin/images")
@RequiredArgsConstructor
public class AdminImageController {

    private final ImageStorageService imageStorageService;

    @PostMapping("/upload")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) {
        String url = imageStorageService.upload(file);
        return Map.of("url", url);
    }
}
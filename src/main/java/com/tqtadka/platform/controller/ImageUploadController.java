package com.tqtadka.platform.controller;

import com.tqtadka.platform.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

//@Controller
public class ImageUploadController {

//    private final ImageUploadService imageUploadService;
//
//    public ImageUploadController(ImageUploadService imageUploadService) {
//        this.imageUploadService = imageUploadService;
//    }
//
//
//    @PostMapping("/admin/images/upload")
//    @ResponseBody
//    public Map<String, String> uploadImage(
//            @RequestParam("file") MultipartFile file) {
//
//        String imageUrl = imageUploadService.upload(file);
//        return Map.of("url", imageUrl);
//    }
}

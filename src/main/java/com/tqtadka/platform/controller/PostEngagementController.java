package com.tqtadka.platform.controller;

import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostEngagementController {

    private final PostService postService;

    public PostEngagementController(PostService postService) {
        this.postService = postService;
    }

    /* =====================================================
       👏 APPLAUSE / LIKE
       ✅ OPEN TO EVERYONE (NO LOGIN)
    ===================================================== */
    @PostMapping("/{lang}/{slug}/applause")
    public ResponseEntity<Void> addApplause(
            @PathVariable String lang,
            @PathVariable String slug
    ) {
        postService.addApplause(slug, resolveLanguage(lang));
        return ResponseEntity.ok().build();
    }

    /* =====================================================
       UTIL
    ===================================================== */
    private LanguageType resolveLanguage(String lang) {
        return "kn".equalsIgnoreCase(lang)
                ? LanguageType.KN
                : LanguageType.EN;
    }
}
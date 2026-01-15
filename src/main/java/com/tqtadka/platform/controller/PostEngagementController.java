package com.tqtadka.platform.controller;

import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostEngagementController {

    private final PostService postService;

    public PostEngagementController(PostService postService) {
        this.postService = postService;
    }

    /* =====================================================
       👏 APPLAUSE / LIKE
       🔐 LOGIN REQUIRED
    ===================================================== */
    @PostMapping("/{lang}/{slug}/applause")
    public ResponseEntity<String> addApplause(
            @PathVariable String lang,
            @PathVariable String slug,
            Authentication authentication
    ) {

        // 🔐 Proper anonymous check (Spring Security 6 safe)
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Please login to give applause");
        }

        postService.addApplause(slug, resolveLanguage(lang));

        return ResponseEntity.ok("Applause added");
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
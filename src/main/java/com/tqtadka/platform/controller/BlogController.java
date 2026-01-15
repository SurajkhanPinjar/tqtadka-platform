package com.tqtadka.platform.controller;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.entity.Post;
import com.tqtadka.platform.service.CommentService;
import com.tqtadka.platform.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BlogController {

    private final PostService postService;
    private final CommentService commentService;

    public BlogController(
            PostService postService,
            CommentService commentService
    ) {
        this.postService = postService;
        this.commentService = commentService;
    }

    /* ===============================
       BLOG VIEW (PUBLIC)
    =============================== */
    @GetMapping("/{lang}/blog/{slug}")
    public String viewPost(
            @PathVariable String lang,
            @PathVariable String slug,
            Model model
    ) {

        LanguageType language = resolveLanguage(lang);
        String resolvedLang = language.name().toLowerCase();

        Post post;
        try {
            // ✅ 1. Fetch post WITH sections (no side effects)
            post = postService.getPublishedPost(slug, language);

            // ✅ 2. Increment views separately (atomic, scalable)
            postService.incrementViews(slug, language);

        } catch (RuntimeException ex) {
            return "error/404";
        }

        // HEADER
        model.addAttribute("lang", resolvedLang);
        model.addAttribute("categories", CategoryType.values());
        model.addAttribute("activeCategory", post.getCategory());

        // BLOG
        model.addAttribute("post", post);

        // COMMENTS
        model.addAttribute(
                "comments",
                commentService.getCommentsForPost(slug, language)
        );

        return "blog/view";
    }

    /* ===============================
       UTIL
    =============================== */
    private LanguageType resolveLanguage(String lang) {
        return "kn".equalsIgnoreCase(lang)
                ? LanguageType.KN
                : LanguageType.EN;
    }
}
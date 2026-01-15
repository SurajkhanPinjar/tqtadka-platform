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

    public BlogController(PostService postService,
                          CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    /* ===============================
       🔁 SAFE REDIRECT (NO LIST PAGE)
       /en/blog  -> /en
       /kn/blog  -> /kn
    =============================== */
    @GetMapping({"/{lang}/blog", "/{lang}/blog/"})
    public String redirectBlogRoot(@PathVariable String lang) {
        LanguageType language = resolveLanguageOrThrow(lang);
        return "redirect:/" + language.name().toLowerCase();
    }

    /* ===============================
       BLOG VIEW (ONLY PAGE)
       /en/blog/{slug}
       /kn/blog/{slug}
    =============================== */
    @GetMapping("/{lang}/blog/{slug}")
    public String viewPost(
            @PathVariable String lang,
            @PathVariable String slug,
            Model model
    ) {

        LanguageType language = resolveLanguageOrThrow(lang);

        Post post;
        try {
            post = postService.getPublishedPost(slug, language);
            postService.incrementViews(slug, language);
        } catch (RuntimeException ex) {
            // ✅ header-safe 404
            model.addAttribute("lang", language.name().toLowerCase());
            model.addAttribute("categories", CategoryType.values());
            model.addAttribute("activeCategory", null);
            return "error/404";
        }

        // Header data
        model.addAttribute("lang", language.name().toLowerCase());
        model.addAttribute("categories", CategoryType.values());
        model.addAttribute("activeCategory", post.getCategory());

        // Page data
        model.addAttribute("post", post);
        model.addAttribute(
                "comments",
                commentService.getCommentsForPost(slug, language)
        );

        return "blog/view"; // ✅ ONLY view page
    }

    /* ===============================
       LANGUAGE RESOLVER (STRICT)
    =============================== */
    private LanguageType resolveLanguageOrThrow(String lang) {
        if ("en".equalsIgnoreCase(lang)) {
            return LanguageType.EN;
        }
        if ("kn".equalsIgnoreCase(lang)) {
            return LanguageType.KN;
        }
        throw new IllegalArgumentException("Unsupported language: " + lang);
    }
}
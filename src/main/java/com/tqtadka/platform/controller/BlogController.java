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

    /* =====================================================
       BLOG VIEW (PUBLIC)
       /en/blog/{slug}
       /kn/blog/{slug}
    ===================================================== */
    @GetMapping("/{lang:en|kn}/blog/{slug}")
    public String viewPost(
            @PathVariable String lang,
            @PathVariable String slug,
            Model model
    ) {

        LanguageType language =
                "kn".equalsIgnoreCase(lang)
                        ? LanguageType.KN
                        : LanguageType.EN;

        Post post;
        try {
            // ✅ MUST fetch with imageSections + sections + prompts
            post = postService.getPostForView(slug, language);

            // ✅ atomic DB update (no entity reload)
            postService.incrementViews(slug, language);

        } catch (RuntimeException ex) {
            model.addAttribute("lang", lang.toLowerCase());
            model.addAttribute("categories", CategoryType.values());
            model.addAttribute("activeCategory", null);
            return "error/404";
        }

        // HEADER DATA
        model.addAttribute("lang", lang.toLowerCase());
        model.addAttribute("categories", CategoryType.values());
        model.addAttribute("activeCategory", post.getCategory());

        // PAGE DATA
        model.addAttribute("post", post);
        model.addAttribute(
                "comments",
                commentService.getCommentsForPost(slug, language)
        );

        return "blog/view";
    }

    /* =====================================================
       SAFE REDIRECTS
    ===================================================== */
    @GetMapping({
            "/{lang:en|kn}/blog",
            "/{lang:en|kn}/blog/"
    })
    public String redirectBlogRoot(@PathVariable String lang) {
        return "redirect:/" + lang.toLowerCase();
    }
}
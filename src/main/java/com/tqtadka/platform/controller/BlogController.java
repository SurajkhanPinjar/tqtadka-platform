package com.tqtadka.platform.controller;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.entity.Post;
import com.tqtadka.platform.repository.PostRepository;
import com.tqtadka.platform.service.CommentService;
import com.tqtadka.platform.service.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@Controller
public class BlogController {

    private final PostService postService;
    private final CommentService commentService;
    private final PostRepository postRepository;

    public BlogController(
            PostService postService,
            CommentService commentService,
            PostRepository postRepository
    ) {
        this.postService = postService;
        this.commentService = commentService;
        this.postRepository = postRepository;
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
            // SAFE READ
            post = postService.getPostForView(slug, language);

            // WRITE in separate TX
            postService.incrementViews(slug, language);

        } catch (RuntimeException ex) {
            model.addAttribute("lang", lang.toLowerCase());
            model.addAttribute("categories", CategoryType.values());
            model.addAttribute("activeCategory", null);
            return "error/404";
        }

        model.addAttribute("lang", lang.toLowerCase());
        model.addAttribute("categories", CategoryType.values());
        model.addAttribute("activeCategory", post.getCategory());

        model.addAttribute("post", post);
        model.addAttribute(
                "comments",
                commentService.getCommentsForPost(slug, language)
        );
        model.addAttribute(
                "recentPosts",
                postRepository.findRecentPostsForSidebar(post.getLanguage())
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
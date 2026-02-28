package com.tqtadka.platform.controller;

import com.tqtadka.platform.dto.RelatedPostView;
import com.tqtadka.platform.dto.SidebarPostView;
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

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    ===================================================== */
    @GetMapping("/{lang:en|kn}/{categorySlug}/{slug}")
    public String viewPost(
            @PathVariable String lang,
            @PathVariable String slug,
            Model model
    ) {

        LanguageType language =
                "kn".equalsIgnoreCase(lang)
                        ? LanguageType.KN
                        : LanguageType.EN;

        try {
            // ✅ PURE READ (no heavy joins, no LOBs)
            Post post = postService.getPostForPublicView(slug, language);

            // =========================
            // MODEL BASICS
            // =========================
            model.addAttribute("lang", lang.toLowerCase());
            model.addAttribute("categories", CategoryType.values());
            model.addAttribute("activeCategory", post.getCategory());
            model.addAttribute("post", post);

            // =========================
            // BREADCRUMB
            // =========================
            CategoryType category = post.getCategory();

            model.addAttribute("categoryName", category.getDisplayName());
            model.addAttribute("categorySlug", category.getSlug());
            model.addAttribute("pageTitle", post.getTitle());

            // =========================
            // COMMENTS
            // =========================
            model.addAttribute(
                    "comments",
                    commentService.getCommentsForPost(slug, language)
            );

            // =========================
            // SIDEBAR
            // =========================
            model.addAttribute(
                    "recentPosts",
                    postRepository.findRecentPostsForSidebar(language)
            );

            // =========================
            // 🔗 RELATED POSTS (SAFE)
            // =========================
            Set<String> relatedSlugs =
                    post.getRelatedPostSlugs() == null
                            ? Set.of()
                            : post.getRelatedPostSlugs();

            List<RelatedPostView> relatedPosts =
                    relatedSlugs.isEmpty()
                            ? List.of()
                            : postRepository.findRelatedPostViews(
                            relatedSlugs,
                            language
                    );

            model.addAttribute("relatedPosts", relatedPosts);


            // =========================
            // 🔥 WRITE IN SEPARATE TX
            // =========================
            postService.incrementViews(slug, language);


            // Trending Posts
            model.addAttribute(
                    "trendingPosts",
                    postRepository.findTrendingPosts(
                            language,
                            PageRequest.of(0, 8)
                    )
            );

            List<SidebarPostView> youMightLikePosts =
                    postRepository.findYouMightLikePosts(
                            post.getCategory(),
                            language,
                            post.getSlug(),
                            PageRequest.of(0, 8)
                    );

            List<Map<String, Object>> faqSchema = post.getFaqs()
                    .stream()
                    .filter(f -> f.getQuestion() != null && f.getAnswer() != null)
                    .map(f -> Map.of(
                            "@type", "Question",
                            "name", f.getQuestion(),
                            "acceptedAnswer", Map.of(
                                    "@type", "Answer",
                                    "text", f.getAnswer()
                            )
                    ))
                    .toList();

            model.addAttribute("faqSchema", faqSchema);

            model.addAttribute("youMightLikePosts", youMightLikePosts);

            return "blog/view";

        } catch (RuntimeException ex) {
            model.addAttribute("lang", lang.toLowerCase());
            model.addAttribute("categories", CategoryType.values());
            model.addAttribute("activeCategory", null);
            return "error/404";
        }
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
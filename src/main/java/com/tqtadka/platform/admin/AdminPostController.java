package com.tqtadka.platform.controller.admin;

import com.tqtadka.platform.entity.*;
import com.tqtadka.platform.service.PostService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin/posts")
public class AdminPostController {

    private final PostService postService;

    public AdminPostController(PostService postService) {
        this.postService = postService;
    }

    /* ============================
       LIST POSTS
    ============================ */
    @GetMapping
    public String listPosts(Model model) {
        model.addAttribute("posts", postService.getAllPostsForAdmin());
        return "admin/posts";
    }

    /* ============================
       CREATE PAGE
    ============================ */
    @GetMapping("/create")
    public String showCreateForm(
            Authentication auth,
            Model model
    ) {

        User user = (User) auth.getPrincipal();

        Set<CategoryType> allowedCategories =
                user.getRole() == Role.ADMIN
                        ? EnumSet.allOf(CategoryType.class)
                        : user.getAllowedCategories();

        model.addAttribute("categories", allowedCategories);
        model.addAttribute("languages", LanguageType.values());

        return "admin/create-post";
    }

    /* ============================
       CREATE POST
    ============================ */
    @PostMapping("/create")
    public String createPost(
            Authentication auth,

            @RequestParam String title,
            @RequestParam(required = false) String intro,
            @RequestParam CategoryType category,
            @RequestParam LanguageType language,
            @RequestParam(required = false) String imageUrl,
            @RequestParam String sectionContent,

            @RequestParam(required = false) String bulletTitle,
            @RequestParam(required = false) String bullets,
            @RequestParam(required = false) String tipTitle,
            @RequestParam(required = false) String tipContent,

            @RequestParam(defaultValue = "true") boolean publish
    ) {

        User user = (User) auth.getPrincipal();

        // 🔐 BACKEND SECURITY CHECK (MANDATORY)
        if (user.getRole() != Role.ADMIN &&
                !user.getAllowedCategories().contains(category)) {
            throw new AccessDeniedException("Category not allowed");
        }

        if (isBlankRichHtml(sectionContent)) {
            return "redirect:/admin/posts/create?error=empty";
        }

        PostSection section = buildSection(
                sectionContent,
                bulletTitle,
                bullets,
                tipTitle,
                tipContent
        );

        postService.createPost(
                title.trim(),
                clean(intro),
                category,
                language,
                clean(imageUrl),
                List.of(section),
                publish
        );

        return "redirect:/admin/posts";
    }

    /* ============================
       EDIT PAGE
    ============================ */
    @GetMapping("/edit/{slug}/{language}")
    public String editPost(
            @PathVariable String slug,
            @PathVariable LanguageType language,
            Authentication auth,
            Model model
    ) {

        User user = (User) auth.getPrincipal();
        Post post = postService.getPostForEdit(slug, language);

        if (user.getRole() != Role.ADMIN &&
                !user.getAllowedCategories().contains(post.getCategory())) {
            throw new AccessDeniedException("Edit not allowed");
        }

        model.addAttribute("post", post);
        model.addAttribute("categories",
                user.getRole() == Role.ADMIN
                        ? EnumSet.allOf(CategoryType.class)
                        : user.getAllowedCategories());

        model.addAttribute("languages", LanguageType.values());
        return "admin/edit-post";
    }

    /* ============================
       SECTION BUILDER
    ============================ */
    private PostSection buildSection(
            String content,
            String bulletTitle,
            String bullets,
            String tipTitle,
            String tipContent
    ) {
        PostSection section = new PostSection();
        section.setContent(content.trim());
        section.setBulletTitle(clean(bulletTitle));
        section.setBullets(normalizeBullets(bullets));
        section.setTipTitle(clean(tipTitle));
        section.setTipContent(clean(tipContent));
        return section;
    }

    /* ============================
       UTIL
    ============================ */
    private String clean(String v) {
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    private boolean isBlankRichHtml(String html) {
        return html == null || html.replaceAll("<[^>]*>", "").trim().isEmpty();
    }

    private String normalizeBullets(String bullets) {
        if (bullets == null || bullets.isBlank()) return null;
        return bullets.replace("\r", "")
                .replaceAll("\n{2,}", "\n")
                .trim();
    }
}
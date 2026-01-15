package com.tqtadka.platform.admin;

import com.tqtadka.platform.entity.*;
import com.tqtadka.platform.security.CustomUserDetails;
import com.tqtadka.platform.service.PostService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

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
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {

        if (userDetails == null) {
            throw new AccessDeniedException("Not authenticated");
        }

        User user = userDetails.getUser();

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
            @AuthenticationPrincipal CustomUserDetails userDetails,

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

            @RequestParam(required = false) Boolean publish
    ) {

        boolean isPublished = Boolean.TRUE.equals(publish);

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
                isPublished
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
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {

        if (userDetails == null) {
            throw new AccessDeniedException("Not authenticated");
        }

        User user = userDetails.getUser();
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
        return html == null ||
                html.replaceAll("<[^>]*>", "")
                        .replace("&nbsp;", "")
                        .trim()
                        .isEmpty();
    }

    private String normalizeBullets(String bullets) {
        if (bullets == null || bullets.isBlank()) return null;
        return bullets.replace("\r", "")
                .replaceAll("\n{2,}", "\n")
                .trim();
    }

    @PostMapping("/toggle-status")
    public String togglePostStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String slug,
            @RequestParam LanguageType language,
            @RequestParam Boolean publish
    ) {

        if (userDetails == null) {
            throw new AccessDeniedException("Not authenticated");
        }

        postService.togglePublishStatus(
                slug,
                language,
                Boolean.TRUE.equals(publish)
        );

        return "redirect:/admin/posts";
    }
}
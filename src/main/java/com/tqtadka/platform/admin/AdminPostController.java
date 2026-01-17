package com.tqtadka.platform.admin;

import com.tqtadka.platform.entity.*;
import com.tqtadka.platform.security.CustomUserDetails;
import com.tqtadka.platform.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
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
       LIST POSTS (ADMIN / AUTHOR)
    ============================ */
    @GetMapping
    public String listPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request,
            Model model
    ) {
        requireAuth(userDetails);

        User currentUser = userDetails.getUser();

        model.addAttribute(
                "posts",
                postService.getPostsForDashboard(currentUser)
        );
        model.addAttribute("currentPath", request.getRequestURI());

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
        requireAuth(userDetails);

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

            // SECTION CONTENT
            @RequestParam String sectionContent,
            @RequestParam(required = false) String bulletTitle,
            @RequestParam(required = false) String bullets,
            @RequestParam(required = false) String tipTitle,
            @RequestParam(required = false) String tipContent,

            // ✅ AI PROMPT META ONLY
            @RequestParam(required = false) AiPostMode aiPostMode,
            @RequestParam(required = false, name = "promptNames") String[] promptNames,

            // 🔥 RAW REQUEST (IMPORTANT)
            HttpServletRequest request,

            @RequestParam(required = false) Boolean publish
    ) {
        requireAuth(userDetails);

        User currentUser = userDetails.getUser();

    /* =========================
       BUILD SECTION (UNCHANGED)
    ========================= */
        PostSection section = buildSection(
                sectionContent,
                bulletTitle,
                bullets,
                tipTitle,
                tipContent
        );

    /* =========================
       🔥 SAFE PROMPT TEXT EXTRACTION
    ========================= */
        String[] rawPromptTexts = request.getParameterValues("promptTexts");

        List<String> promptTextList =
                rawPromptTexts == null
                        ? List.of()
                        : List.of(rawPromptTexts).stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

    /* =========================
       CALL SERVICE (SAFE)
    ========================= */
        postService.createPost(
                title.trim(),
                clean(intro),
                category,
                language,
                clean(imageUrl),
                List.of(section),
                Boolean.TRUE.equals(publish),
                currentUser,
                aiPostMode,                  // ✅ OK
                promptNames,                 // ✅ names are safe
                promptTextList.toArray(new String[0]) // ✅ NO COMMA SPLIT
        );

        return "redirect:/admin/posts";
    }

    /* ============================
       EDIT PAGE (SECURE)
    ============================ */
    @GetMapping("/edit/{postId}")
    public String editPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        requireAuth(userDetails);

        User currentUser = userDetails.getUser();
        Post post = postService.getPostForEdit(postId, currentUser);

        model.addAttribute("post", post);
        model.addAttribute("categories",
                currentUser.getRole() == Role.ADMIN
                        ? EnumSet.allOf(CategoryType.class)
                        : currentUser.getAllowedCategories()
        );

        return "admin/edit-post";
    }
    @PostMapping("/update/{postId}")
    public String updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @RequestParam String title,
            @RequestParam(required = false) String intro,
            @RequestParam CategoryType category,
            @RequestParam LanguageType language,
            @RequestParam(required = false) String imageUrl,

            // SECTION CONTENT
            @RequestParam String sectionContent,
            @RequestParam(required = false) String bulletTitle,
            @RequestParam(required = false) String bullets,
            @RequestParam(required = false) String tipTitle,
            @RequestParam(required = false) String tipContent,

            // ✅ AI PROMPT META ONLY
            @RequestParam(required = false) AiPostMode aiPostMode,
            @RequestParam(required = false, name = "promptNames") String[] promptNames,

            // 🔥 RAW REQUEST (IMPORTANT)
            HttpServletRequest request,

            @RequestParam(required = false) Boolean publish
    ) {
        requireAuth(userDetails);

        User currentUser = userDetails.getUser();

    /* =========================
       BUILD SECTION (UNCHANGED)
    ========================= */
        PostSection section = buildSection(
                sectionContent,
                bulletTitle,
                bullets,
                tipTitle,
                tipContent
        );

    /* =========================
       🔥 SAFE PROMPT TEXT EXTRACTION
    ========================= */
        String[] rawPromptTexts = request.getParameterValues("promptTexts");

        List<String> promptTextList =
                rawPromptTexts == null
                        ? List.of()
                        : List.of(rawPromptTexts).stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

    /* =========================
       SERVICE CALL (SAFE)
    ========================= */
        postService.updatePost(
                postId,
                title.trim(),
                clean(intro),
                category,
                language,
                clean(imageUrl),
                List.of(section),
                Boolean.TRUE.equals(publish),
                currentUser,
                aiPostMode,
                promptNames,
                promptTextList.toArray(new String[0]) // ✅ NO COMMA SPLIT
        );

        return "redirect:/admin/posts";
    }

    /* ============================
       DELETE POST
    ============================ */
    @PostMapping("/delete")
    public String deletePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long postId
    ) {
        requireAuth(userDetails);

        postService.deletePost(postId, userDetails.getUser());
        return "redirect:/admin/posts";
    }

    /* ============================
       HELPERS
    ============================ */
    private void requireAuth(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new AccessDeniedException("Not authenticated");
        }
    }

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

    private String clean(String v) {
        return (v == null || v.isBlank()) ? null : v.trim();
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
            @RequestParam Long postId,
            @RequestParam Boolean publish
    ) {
        if (userDetails == null) {
            throw new AccessDeniedException("Not authenticated");
        }

        postService.togglePublishStatus(
                postId,
                publish,
                userDetails.getUser()
        );

        return "redirect:/admin/posts";
    }
}
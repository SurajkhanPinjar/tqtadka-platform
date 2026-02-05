package com.tqtadka.platform.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqtadka.platform.dto.ImageSectionDto;
import com.tqtadka.platform.entity.*;
import com.tqtadka.platform.security.CustomUserDetails;
import com.tqtadka.platform.service.PostService;
import com.tqtadka.platform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/posts")
@Slf4j
public class AdminPostController {

    private final PostService postService;

    private final UserService userService;



    public AdminPostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;

    }

    /* ============================
       LIST POSTS (ADMIN / AUTHOR)
    ============================ */
//    @GetMapping
//    public String listPosts(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            HttpServletRequest request,
//            Model model
//    ) {
//        requireAuth(userDetails);
//
//        User currentUser = userDetails.getUser();
//
//        model.addAttribute(
//                "posts",
//                postService.getPostsForDashboard(currentUser)
//        );
//        model.addAttribute("currentPath", request.getRequestURI());
//
//        return "admin/posts";
//    }
    @GetMapping
    public String listPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) LanguageType lang,
            @RequestParam(required = false) CategoryType category,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Boolean published,
            Model model
    ) {
        requireAuth(userDetails);

        List<Post> posts = postService.findAdminPosts(
                q, lang, category, authorId, published, userDetails.getUser()
        );

        model.addAttribute("posts", posts);

        // keep filter state
        model.addAttribute("q", q);
        model.addAttribute("lang", lang);
        model.addAttribute("category", category);
        model.addAttribute("authorId", authorId);
        model.addAttribute("published", published);

        model.addAttribute("categories", CategoryType.values());
        model.addAttribute("authors", userService.findAllAuthors());

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

        // ✅ REQUIRED: empty Post for create
        Post post = new Post();
        post.setRelatedPostSlugs(new HashSet<>());
        post.setTags(new HashSet<>());

        model.addAttribute("post", post);
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
            @RequestParam(required = false) String existingImageUrl,

            // SECTION CONTENT
            @RequestParam String sectionContent,
            @RequestParam(required = false) String bulletTitle,
            @RequestParam(required = false) String bullets,
            @RequestParam(required = false) String tipTitle,
            @RequestParam(required = false) String tipContent,

            // AI PROMPT META
            @RequestParam(required = false) AiPostMode aiPostMode,
            @RequestParam(required = false, name = "promptNames") String[] promptNames,

            // IMAGE SECTIONS JSON
            @RequestParam(required = false) String imageSectionsJson,

            HttpServletRequest request,
            @RequestParam(required = false) Boolean publish,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) List<String> relatedSlugs
    ) {
        requireAuth(userDetails);
        User currentUser = userDetails.getUser();

    /* =========================
       BUILD MAIN SECTION
    ========================= */
        PostSection section = buildSection(
                sectionContent,
                bulletTitle,
                bullets,
                tipTitle,
                tipContent
        );
        log.info("IMAGE URL RECEIVED: {}", imageUrl);
    /* =========================
       SAFE PROMPT TEXT EXTRACTION
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
       🔥 IMAGE SECTION PAYLOAD FIX
    ========================= */
        String safeImageSectionsJson =
                (imageSectionsJson == null ||
                        imageSectionsJson.isBlank() ||
                        imageSectionsJson.equals("[]"))
                        ? null
                        : imageSectionsJson;

    /* =========================
       CALL SERVICE
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
                aiPostMode,
                promptNames,
                promptTextList.toArray(new String[0]),
                safeImageSectionsJson,   // ✅ SAFE
                tags,
                relatedSlugs
        );

        return "redirect:/admin/posts";
    }

    @GetMapping("/edit/{postId}")
    public String editPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        requireAuth(userDetails);

        User currentUser = userDetails.getUser();

        // 🔐 Security: ownership + role check happens here
        Post post = postService.getPostForEdit(postId, currentUser);

    /* ===============================
       IMAGE SECTIONS (FLATTEN)
    ============================== */
        List<Map<String, String>> imageSectionDtos =
                post.getImageSections()
                        .stream()
                        .sorted(Comparator.comparingInt(PostImageSection::getDisplayOrder))
                        .map(s -> Map.of(
                                "imageUrl", s.getImageUrl(),
                                "heading", s.getHeading(),
                                "description", s.getDescription()
                        ))
                        .toList();

    /* ===============================
       TAG STRING
    ============================== */
        String tagString =
                post.getTags() == null
                        ? ""
                        : post.getTags()
                        .stream()
                        .map(Tag::getName)
                        .collect(Collectors.joining(", "));

    /* ===============================
       CATEGORY ACCESS CONTROL (✅ FIXED)
    ============================== */
        List<CategoryType> allowedCategories =
                new ArrayList<>(currentUser.getEffectiveCategories());

        // 🔥 SAFETY: ensure post category is visible even if permissions changed
        if (!allowedCategories.contains(post.getCategory())) {
            allowedCategories.add(0, post.getCategory());
        }

    /* ===============================
       MODEL
    ============================== */
        model.addAttribute("post", post);
        model.addAttribute("imageSectionDtos", imageSectionDtos);
        model.addAttribute("tagString", tagString);
        model.addAttribute("relatedSlugs", post.getRelatedPostSlugs());
        model.addAttribute("categories", allowedCategories);

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

            // 🔥 MAIN IMAGE (FIXED)
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String existingImageUrl,

            // SECTION CONTENT
            @RequestParam String sectionContent,
            @RequestParam(required = false) String bulletTitle,
            @RequestParam(required = false) String bullets,
            @RequestParam(required = false) String tipTitle,
            @RequestParam(required = false) String tipContent,

            // AI
            @RequestParam(required = false) AiPostMode aiPostMode,
            @RequestParam(required = false, name = "promptNames") String[] promptNames,

            // 🔥 IMAGE SECTIONS JSON
            @RequestParam(required = false) String imageSectionsJson,
            @RequestParam(required = false) Boolean removeImage,

            @RequestParam(required = false) String tags,
            HttpServletRequest request,
            @RequestParam(required = false) Boolean publish,
            @RequestParam(required = false) List<String> relatedSlugs

    ) {
        requireAuth(userDetails);
        User currentUser = userDetails.getUser();

    /* =========================
       BUILD MAIN SECTION
    ========================= */
        PostSection section = buildSection(
                sectionContent,
                bulletTitle,
                bullets,
                tipTitle,
                tipContent
        );

    /* =========================
       AI PROMPTS (UNCHANGED)
    ========================= */
        String[] rawPromptTexts = request.getParameterValues("promptTexts");

        List<String> promptTextList =
                rawPromptTexts == null
                        ? List.of()
                        : Arrays.stream(rawPromptTexts)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

    /* =========================
       🔥 PARSE IMAGE SECTIONS JSON
    ========================= */
        List<PostImageSection> imageSections = List.of();

        if (imageSectionsJson != null && !imageSectionsJson.isBlank()) {
            try {
                ObjectMapper mapper = new ObjectMapper();

                List<ImageSectionDto> dtos = mapper.readValue(
                        imageSectionsJson,
                        new TypeReference<List<ImageSectionDto>>() {}
                );

                imageSections = dtos.stream()
                        .map(dto -> {
                            PostImageSection s = new PostImageSection();
                            s.setImageUrl(dto.getImageUrl());
                            s.setHeading(dto.getHeading());
                            s.setDescription(dto.getDescription());
                            s.setDisplayOrder(dto.getOrder()); // ✅ CRITICAL
                            return s;
                        })
                        .toList();

            } catch (Exception e) {
                log.error("Failed to parse imageSectionsJson", e);
            }
        }

    /* =========================
       🔥 SERVICE CALL (NO REGRESSION)
    ========================= */
        postService.updatePost(
                postId,
                title.trim(),
                clean(intro),
                category,
                language,

                clean(imageUrl),          // new image (optional)
                clean(existingImageUrl),  // 🔥 old image (backup)
                Boolean.TRUE.equals(removeImage),

        List.of(section),
                imageSections,
                Boolean.TRUE.equals(publish),
                currentUser,
                aiPostMode,
                promptNames,
                promptTextList.toArray(new String[0]),
                tags,
                relatedSlugs
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

    @PostMapping("/admin/posts/{postId}/image-section")
    public String addImageSection(
            @PathVariable Long postId,
            @RequestParam String heading,
            @RequestParam String description,
            @RequestParam(required = false) String imageUrl,
            @RequestParam int order
    ) {
        postService.addImageSection(postId, heading, description, imageUrl, order);
        return "redirect:/admin/posts/edit/" + postId;
    }

    @GetMapping("/guidelines")
    public String creatorGuidelines(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "admin/guidelines";
    }
}
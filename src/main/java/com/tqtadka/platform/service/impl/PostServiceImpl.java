package com.tqtadka.platform.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqtadka.platform.dto.ImageSectionDto;
import com.tqtadka.platform.entity.*;
import com.tqtadka.platform.repository.PostImageSectionRepository;
import com.tqtadka.platform.repository.PostRepository;
import com.tqtadka.platform.repository.PostViewEventRepository;
import com.tqtadka.platform.repository.TagRepository;
import com.tqtadka.platform.service.PostService;
import com.tqtadka.platform.util.SlugUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final PostImageSectionRepository imageSectionRepository;


    private final PostViewEventRepository postViewEventRepository;

    @Autowired
    private TagRepository tagRepository;


    public PostServiceImpl(PostRepository postRepository, PostImageSectionRepository imageSectionRepository, PostViewEventRepository postViewEventRepository) {
        this.postRepository = postRepository;
        this.imageSectionRepository = imageSectionRepository;
        this.postViewEventRepository = postViewEventRepository;

    }

    /* =====================================================
       PUBLIC READ
    ===================================================== */
    @Override
    @Transactional(readOnly = true)
    public List<Post> getPublishedPosts(LanguageType language) {
        return postRepository
                .findByLanguageAndPublishedTrueOrderByPublishedAtDesc(language);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPostsByCategory(CategoryType category, LanguageType language) {
        return postRepository
                .findByCategoryAndLanguageAndPublishedTrueOrderByPublishedAtDesc(
                        category, language
                );
    }

    public List<Post> getPostsByCategory(
            CategoryType category,
            LanguageType language,
            String sort
    ) {

        Sort sortSpec;

        switch (sort) {
            case "popular" ->
                    sortSpec = Sort.by(Sort.Direction.DESC, "views");

            case "oldest" ->
                    sortSpec = Sort.by(Sort.Direction.ASC, "publishedAt");

            case "latest" ->
                    sortSpec = Sort.by(Sort.Direction.DESC, "publishedAt");

            default ->
                    sortSpec = Sort.by(Sort.Direction.DESC, "publishedAt");
        }

        return postRepository
                .findByCategoryAndLanguageAndPublishedTrue(
                        category,
                        language,
                        sortSpec
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Post getPublishedPost(String slug, LanguageType language) {
        return postRepository
                .findPublishedPostWithSectionsAndPrompts(slug, language)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    /* =====================================================
       DASHBOARD (ADMIN / AUTHOR)
    ===================================================== */
    @Override
    @Transactional(readOnly = true)
    public List<Post> getPostsForDashboard(User currentUser) {

        if (currentUser.getRole() == Role.ADMIN) {
            return postRepository.findAllByOrderByCreatedAtDesc();
        }

        return postRepository.findByCreatedByOrderByCreatedAtDesc(currentUser);
    }

    /* =====================================================
       EDIT (ROLE SAFE)
    ===================================================== */
//    @Transactional(readOnly = true)
//    @Override
//    public Post getPostForEdit(Long postId, User user) {
//
//        Post post = postRepository
//                .findPostForEdit(postId)
//                .orElseThrow(() -> new RuntimeException("Post not found"));
//
//        // security check
//        if (user.getRole() != Role.ADMIN &&
//                !post.getCreatedBy().getId().equals(user.getId())) {
//            throw new AccessDeniedException("Not allowed");
//        }
//
//        return post;
//    }

    @Transactional(readOnly = true)
    @Override
    public Post getPostForEdit(Long postId, User user) {
        Post post = postRepository.findPostForEditFull(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // optional auth check
        if (!user.isAdmin() && !post.getCreatedBy().equals(user)) {
            throw new RuntimeException("Unauthorized");
        }

        return post;
    }

    /* =====================================================
       DELETE (ROLE SAFE)
    ===================================================== */
    @Override
    public void deletePost(Long postId, User currentUser) {
        Post post = getPostForEdit(postId, currentUser);
        postRepository.delete(post);
    }

    /* =====================================================
       ✅ FIXED: TOGGLE PUBLISH STATUS
    ===================================================== */
    @Override
    public void togglePublishStatus(Long postId, boolean publish, User currentUser) {

        Post post = getPostForEdit(postId, currentUser);

        post.setPublished(publish);

        if (publish) {
            post.setPublishedAt(
                    post.getPublishedAt() != null
                            ? post.getPublishedAt()
                            : LocalDateTime.now()
            );
        } else {
            post.setPublishedAt(null);
        }
    }
    /* =====================================================
       ENGAGEMENT
    ===================================================== */
    @Override
    @Transactional
    public void incrementViews(String slug, LanguageType language) {

        Post post = postRepository
                .findBySlugAndLanguageAndPublishedTrue(slug, language)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 1️⃣ increment counter (fast aggregate)
        post.setViews(post.getViews() + 1);

        // 2️⃣ save view event (analytics source)
        PostViewEvent event = PostViewEvent.builder()
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        postViewEventRepository.save(event);

        // 3️⃣ persist post update
        postRepository.save(post);
    }

    @Override
    public void addApplause(String slug, LanguageType language) {
        postRepository.incrementApplause(slug, language);
    }

    @Override
    public void incrementCommentCount(String slug, LanguageType language) {
        postRepository.incrementCommentCount(slug, language);
    }

    /* =====================================================
       CREATE
    ===================================================== */
    @Override
    public Post createPost(
            String title,
            String intro,
            CategoryType category,
            LanguageType language,
            String imageUrl,
            List<PostSection> sections,
            boolean publish,
            User currentUser,
            AiPostMode aiPostMode,
            String[] promptNames,
            String[] promptTexts,
            String imageSectionsJson,
            String tagsInput,
            List<String> relatedSlugs
    ) {

        String baseSlug = SlugUtil.toSlug(title);
        String slug = generateUniqueSlug(baseSlug, language);

        Post post = Post.builder()
                .title(title.trim())
                .slug(slug)
                .intro(clean(intro))
                .category(category)
                .language(language)
                .imageUrl(clean(imageUrl))
                .published(publish)
                .createdBy(currentUser)
                .authorName(currentUser.getName())
                .createdAt(LocalDateTime.now())
                .publishedAt(publish ? LocalDateTime.now() : null)
                .sections(new HashSet<>())
                .aiPrompts(new HashSet<>())
                .imageSections(new HashSet<>())   // 🔥 SAFE INIT
                .tags(new HashSet<>())
                .aiPostMode(
                        category == CategoryType.AI
                                ? (aiPostMode != null ? aiPostMode : AiPostMode.BLOG)
                                : null
                )
                .build();

        // ===============================
        // EXISTING LOGIC (UNCHANGED)
        // ===============================
        attachSingleSection(post, sections);
        attachAiPrompts(post, category, promptNames, promptTexts);

        // ===============================
// 🔖 TAGS (SAFE & OPTIONAL)
// ===============================
        Set<Tag> tags = resolveTags(tagsInput);
        post.setTags(tags);

        // ===============================
        // 🔥 IMAGE SECTIONS (SAFE ADD)
        // ===============================
        if (imageSectionsJson != null && !imageSectionsJson.isBlank()) {

            try {
                ObjectMapper mapper = new ObjectMapper();

                List<ImageSectionDto> imageSections =
                        mapper.readValue(
                                imageSectionsJson,
                                new TypeReference<List<ImageSectionDto>>() {}
                        );

                for (ImageSectionDto dto : imageSections) {

                    PostImageSection section = new PostImageSection();
                    section.setHeading(dto.getHeading());
                    section.setDescription(dto.getDescription());
                    section.setImageUrl(dto.getImageUrl());
                    section.setDisplayOrder(dto.getOrder());
                    section.setPost(post); // 🔥 IMPORTANT

                    post.getImageSections().add(section);
                }

            } catch (Exception e) {
                throw new RuntimeException("Invalid image section payload", e);
            }
        }

        post.setRelatedPostSlugs(
                normalizeRelatedSlugs(relatedSlugs)
        );

        return postRepository.save(post);
    }
    /* =====================================================
       UPDATE
    ===================================================== */
    @Override
    public Post updatePost(
            Long postId,
            String title,
            String intro,
            CategoryType category,
            LanguageType language,
            String imageUrl,
            List<PostSection> sections,
            List<PostImageSection> imageSections,
            boolean publish,
            User currentUser,
            AiPostMode aiPostMode,
            String[] promptNames,
            String[] promptTexts,
            String tags,
            List<String> relatedSlugs
    ) {

        Post post = getPostForEdit(postId, currentUser);

        post.setTitle(title.trim());
        post.setIntro(clean(intro));
        post.setCategory(category);
        post.setLanguage(language);
        post.setImageUrl(clean(imageUrl));
        post.setPublished(publish);

        post.setPublishedAt(
                publish
                        ? (post.getPublishedAt() != null
                        ? post.getPublishedAt()
                        : LocalDateTime.now())
                        : null
        );

        post.setAiPostMode(
                category == CategoryType.AI
                        ? (aiPostMode != null ? aiPostMode : AiPostMode.BLOG)
                        : null
        );

        // 🔥 CLEAR OLD DATA
        post.getSections().clear();
        post.getAiPrompts().clear();
        post.getImageSections().clear();
        post.getTags().clear();                 // ✅ TAGS CLEAR

        // 🔥 ATTACH NEW DATA
        attachSingleSection(post, sections);
        attachAiPrompts(post, category, promptNames, promptTexts);
        attachImageSections(post, imageSections);

        // 🔥 TAGS (SAFE, CLEAN)
        if (tags != null && !tags.isBlank()) {
            Set<Tag> resolvedTags = resolveTags(tags);
            post.getTags().addAll(resolvedTags);
        }

        // ✅ safe mutation
        post.getRelatedPostSlugs().clear();

        if (relatedSlugs != null) {
            post.getRelatedPostSlugs().addAll(relatedSlugs);
        }

        return postRepository.save(post);
    }
    /* =====================================================
       🔥 SINGLE SECTION FIX (CORE FIX)
    ===================================================== */
    private void attachSingleSection(Post post, List<PostSection> sections) {
        if (sections == null || sections.isEmpty()) return;

        PostSection src = sections.get(0);

        PostSection section = new PostSection();
        section.setPost(post);
        section.setContent(src.getContent());
        section.setBulletTitle(src.getBulletTitle());
        section.setBullets(src.getBullets());
        section.setTipTitle(src.getTipTitle());
        section.setTipContent(src.getTipContent());

        post.getSections().add(section);
    }

    /* =====================================================
       🔥 AI PROMPTS (NO COMMA SPLIT, SAFE)
    ===================================================== */
    private void attachAiPrompts(
            Post post,
            CategoryType category,
            String[] promptNames,
            String[] promptTexts
    ) {
        if (category != CategoryType.AI) return;
        if (post.getAiPostMode() != AiPostMode.PROMPT) return;
        if (promptNames == null || promptTexts == null) return;

        int count = Math.min(promptNames.length, promptTexts.length);

        for (int i = 0; i < count; i++) {
            String name = clean(promptNames[i]);
            String text = clean(promptTexts[i]);

            if (name == null || text == null) continue;

            AiPrompt prompt = AiPrompt.builder()
                    .name(name)
                    .promptText(text)
                    .position(i + 1)
                    .post(post)
                    .build();

            post.getAiPrompts().add(prompt);
        }
    }

    /* =====================================================
       HELPERS
    ===================================================== */
    private String clean(String v) {
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    private String generateUniqueSlug(String baseSlug, LanguageType language) {
        String slug = baseSlug;
        int counter = 1;

        while (postRepository.existsBySlugAndLanguage(slug, language)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }



        @Override
        public void addImageSection(
                Long postId,
                String heading,
                String description,
                String imageUrl,
                int order
        ) {

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("Post not found"));

            PostImageSection section = new PostImageSection();
            section.setPost(post);
            section.setHeading(heading);
            section.setDescription(description);
            section.setImageUrl(imageUrl);
            section.setDisplayOrder(order);

            imageSectionRepository.save(section);
    }

    @Transactional(readOnly = true)
    @Override
    public Post getPostForView(String slug, LanguageType language) {

        Post post = postRepository
                .findPostForView(slug, language)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 🔥 FORCE INITIALIZATION (CRITICAL)
        post.getTags().size();
        post.getSections().size();
        post.getImageSections().size();
        post.getRelatedPostSlugs().size();

        if (post.getAiPrompts() != null) {
            post.getAiPrompts().size();
        }

        return post;
    }

    @Transactional(readOnly = true)
    public Post getPostForPublicView(String slug, LanguageType language) {
        Post post = postRepository.findPostForPublicView(slug, language)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // ✅ FORCE INIT (cheap & safe)
        post.getTags().size();                // tags
        post.getRelatedPostSlugs().size();    // related blogs

        return post;
    }

    private void attachImageSections(
            Post post,
            List<PostImageSection> imageSections
    ) {
        if (imageSections == null || imageSections.isEmpty()) {
            return;
        }

        int order = 1;

        for (PostImageSection s : imageSections) {
            s.setId(null);              // force INSERT
            s.setPost(post);            // owning side
            s.setDisplayOrder(order++); // 🔥 ALWAYS normalize order

            post.getImageSections().add(s);
        }
    }

    // tag

    private Set<Tag> resolveTags(String tagInput) {

        if (tagInput == null || tagInput.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(tagInput.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(this::getOrCreateTag)
                .collect(Collectors.toSet());
    }

    private Tag getOrCreateTag(String name) {
        String slug = name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        return tagRepository.findBySlug(slug)
                .orElseGet(() ->
                        tagRepository.save(
                                Tag.builder()
                                        .name(name)
                                        .slug(slug)
                                        .build()
                        )
                );
    }

    private Set<String> normalizeRelatedSlugs(List<String> slugs) {
        if (slugs == null) return Set.of();

        return slugs.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s
                        .replaceAll("^/+", "")
                        .replaceAll(".*/blog/", "")
                )
                .collect(Collectors.toSet()); // 👈 IMPORTANT
    }

    @Transactional(readOnly = true)
    public List<Post> search(String q, LanguageType lang) {

        Map<Long, Post> merged = new LinkedHashMap<>();

        postRepository
                .searchByTitle(q, lang, PageRequest.of(0, 20))
                .forEach(p -> merged.put(p.getId(), p));

        postRepository
                .searchByTags(q, lang)
                .forEach(p -> merged.putIfAbsent(p.getId(), p));

        return new ArrayList<>(merged.values());
    }

    public Page<Post> getPostsByCategory(
            CategoryType category,
            LanguageType language,
            String sort,
            int page
    ) {

        Sort sortSpec = switch (sort) {
            case "popular" -> Sort.by(Sort.Direction.DESC, "views");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "publishedAt");
            default -> Sort.by(Sort.Direction.DESC, "publishedAt"); // latest
        };

        Pageable pageable = PageRequest.of(
                page,
                9,                // 🔥 9 cards
                sortSpec
        );

        return postRepository
                .findByCategoryAndLanguageAndPublishedTrue(
                        category,
                        language,
                        pageable
                );
    }

    public Page<Post> getPostsByCategory(
            CategoryType category,
            LanguageType language,
            String sort,
            int page,
            Boolean promptOnly
    ) {
        Sort sortSpec = switch (sort) {
            case "popular" -> Sort.by(Sort.Direction.DESC, "views");
            case "oldest"  -> Sort.by(Sort.Direction.ASC, "publishedAt");
            default        -> Sort.by(Sort.Direction.DESC, "publishedAt");
        };

        Pageable pageable = PageRequest.of(page, 9, sortSpec);

        if (category == CategoryType.AI && Boolean.TRUE.equals(promptOnly)) {
            return postRepository.findAiPromptPosts(
                    language, pageable
            );
        }

        return postRepository.findByCategoryAndLanguageAndPublishedTrue(
                category, language, pageable
        );
    }




}
package com.tqtadka.platform.service.impl;

import com.tqtadka.platform.entity.*;
import com.tqtadka.platform.repository.PostRepository;
import com.tqtadka.platform.service.PostService;
import com.tqtadka.platform.util.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /* =====================================================
       CREATE (ADMIN / AUTHOR)
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
            List<String> promptNames,
            List<String> promptTexts
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
                .sections(new ArrayList<>())
                .aiPrompts(new HashSet<>())
                .aiPostMode(
                        category == CategoryType.AI
                                ? (aiPostMode != null ? aiPostMode : AiPostMode.BLOG)
                                : null
                )
                .build();

        /* ---------- Sections (UNCHANGED) ---------- */
        if (sections != null) {
            sections.forEach(section -> {
                section.setPost(post);
                post.getSections().add(section);
            });
        }

        /* ---------- AI PROMPTS (SAFE & ISOLATED) ---------- */
        if (category == CategoryType.AI
                && post.getAiPostMode() == AiPostMode.PROMPT
                && promptNames != null
                && promptTexts != null) {

            for (int i = 0; i < promptNames.size(); i++) {

                String name = clean(promptNames.get(i));
                String text = clean(promptTexts.get(i));

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

        return postRepository.save(post);
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

    @Override
    @Transactional(readOnly = true)
    public Post getPublishedPost(String slug, LanguageType language) {
        return postRepository
                .findPublishedPostWithSections(slug, language)
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
    @Override
    @Transactional(readOnly = true)
    public Post getPostForEdit(Long postId, User currentUser) {

        if (currentUser.getRole() == Role.ADMIN) {
            return postRepository.findForEditByAdmin(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
        }

        return postRepository.findForEditByAuthor(postId, currentUser)
                .orElseThrow(() -> new RuntimeException("Access denied"));
    }

    /* =====================================================
       UPDATE (ROLE SAFE)
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
            boolean publish,
            User currentUser,
            AiPostMode aiPostMode,
            List<String> promptNames,
            List<String> promptTexts
    ) {

        Post post = getPostForEdit(postId, currentUser);

        post.setTitle(title.trim());
        post.setIntro(clean(intro));
        post.setCategory(category);
        post.setLanguage(language);
        post.setImageUrl(clean(imageUrl));
        post.setPublished(publish);

        if (publish && post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }
        if (!publish) {
            post.setPublishedAt(null);
        }

        /* ---------- Sections (UNCHANGED) ---------- */
        post.getSections().clear();
        if (sections != null) {
            sections.forEach(section -> {
                section.setPost(post);
                post.getSections().add(section);
            });
        }

        /* ---------- AI MODE ---------- */
        if (category == CategoryType.AI) {
            post.setAiPostMode(aiPostMode != null ? aiPostMode : AiPostMode.BLOG);
        } else {
            post.setAiPostMode(null);
            post.getAiPrompts().clear();
        }

        /* ---------- AI PROMPTS ---------- */
        post.getAiPrompts().clear();

        if (category == CategoryType.AI
                && post.getAiPostMode() == AiPostMode.PROMPT
                && promptNames != null
                && promptTexts != null) {

            for (int i = 0; i < promptNames.size(); i++) {

                String name = clean(promptNames.get(i));
                String text = clean(promptTexts.get(i));

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

        return postRepository.save(post);
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
    public void incrementViews(String slug, LanguageType language) {
        postRepository.incrementViews(slug, language);
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
}
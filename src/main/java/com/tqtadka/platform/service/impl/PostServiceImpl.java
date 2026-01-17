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
            String[] promptTexts
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
                .aiPostMode(
                        category == CategoryType.AI
                                ? (aiPostMode != null ? aiPostMode : AiPostMode.BLOG)
                                : null
                )
                .build();

        attachSingleSection(post, sections);
        attachAiPrompts(post, category, promptNames, promptTexts);

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
            boolean publish,
            User currentUser,
            AiPostMode aiPostMode,
            String[] promptNames,
            String[] promptTexts
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
                        ? (post.getPublishedAt() != null ? post.getPublishedAt() : LocalDateTime.now())
                        : null
        );

        post.setAiPostMode(
                category == CategoryType.AI
                        ? (aiPostMode != null ? aiPostMode : AiPostMode.BLOG)
                        : null
        );

        post.getSections().clear();
        post.getAiPrompts().clear();

        attachSingleSection(post, sections);
        attachAiPrompts(post, category, promptNames, promptTexts);

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
}
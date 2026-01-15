package com.tqtadka.platform.service.impl;

import com.tqtadka.platform.entity.*;
import com.tqtadka.platform.repository.PostRepository;
import com.tqtadka.platform.service.PostService;
import com.tqtadka.platform.util.SlugUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /* =====================================================
       CREATE (ADMIN)
    ===================================================== */
    @Override
    @Transactional
    public Post createPost(
            String title,
            String intro,
            CategoryType category,
            LanguageType language,
            String imageUrl,
            List<PostSection> sections,
            boolean publish
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
                .views(0)
                .applauseCount(0)
                .commentCount(0)
                .publishedAt(publish ? LocalDateTime.now() : null)
                .sections(new ArrayList<>())
                .build();

        if (sections != null) {
            for (PostSection section : sections) {
                section.setPost(post);
                post.getSections().add(section);
            }
        }

        return postRepository.save(post);
    }

    /* =====================================================
       PUBLIC READ
    ===================================================== */
    @Override
    public List<Post> getPublishedPosts(LanguageType language) {
        return postRepository
                .findByLanguageAndPublishedTrueOrderByPublishedAtDesc(language);
    }

    @Override
    public List<Post> getPostsByCategory(CategoryType category, LanguageType language) {
        return postRepository
                .findByCategoryAndLanguageAndPublishedTrueOrderByPublishedAtDesc(
                        category, language
                );
    }

    /**
     * 📖 Blog detail page
     * ✅ fetch published post WITH sections
     * ❌ no side effects
     */
    @Override
    public Post getPublishedPost(String slug, LanguageType language) {
        return postRepository
                .findPublishedPostWithSections(slug, language)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    /* =====================================================
       ENGAGEMENT
    ===================================================== */

    @Override
    @Transactional
    public void incrementViews(String slug, LanguageType language) {
        postRepository.incrementViews(slug, language);
    }

    @Override
    @Transactional
    public void addApplause(String slug, LanguageType language) {
        postRepository.incrementApplause(slug, language);
    }

    @Override
    @Transactional
    public void incrementCommentCount(String slug, LanguageType language) {
        postRepository.incrementCommentCount(slug, language);
    }

    /* =====================================================
       ADMIN READ
    ===================================================== */
    @Override
    public List<Post> getAllPostsForAdmin() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public Post getPostForEdit(String slug, LanguageType language) {
        return postRepository
                .findBySlugAndLanguage(slug, language)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    /* =====================================================
       UPDATE (ADMIN)
    ===================================================== */
    @Override
    @Transactional
    public Post updatePost(
            String slug,
            String title,
            String intro,
            CategoryType category,
            LanguageType language,
            String imageUrl,
            List<PostSection> sections,
            boolean publish
    ) {

        Post post = postRepository
                .findBySlugAndLanguage(slug, language)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(title.trim());
        post.setIntro(clean(intro));
        post.setCategory(category);
        post.setLanguage(language);
        post.setImageUrl(clean(imageUrl));

        if (publish && !post.isPublished()) {
            post.setPublishedAt(LocalDateTime.now());
        }
        if (!publish) {
            post.setPublishedAt(null);
        }

        post.setPublished(publish);

        post.getSections().clear();

        if (sections != null) {
            for (PostSection section : sections) {
                section.setPost(post);
                post.getSections().add(section);
            }
        }

        return post;
    }

    /* =====================================================
       DELETE
    ===================================================== */
    @Override
    @Transactional
    public void deletePost(String slug, LanguageType language) {
        Post post = postRepository
                .findBySlugAndLanguage(slug, language)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        postRepository.delete(post);
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
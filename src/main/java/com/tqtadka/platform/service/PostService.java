package com.tqtadka.platform.service;

import com.tqtadka.platform.entity.*;

import java.util.List;

public interface PostService {

    /* =====================================================
       CREATE
    ===================================================== */

    /**
     * Create a new post.
     *
     * RULES:
     * - Slug generated internally
     * - Slug unique per language
     * - createdBy must be set
     */
    Post createPost(
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
    );

    /* =====================================================
       PUBLIC READ
    ===================================================== */

    List<Post> getPublishedPosts(LanguageType language);

    List<Post> getPostsByCategory(
            CategoryType category,
            LanguageType language
    );

    Post getPublishedPost(
            String slug,
            LanguageType language
    );

    /* =====================================================
       ENGAGEMENT
    ===================================================== */

    void incrementViews(String slug, LanguageType language);

    void addApplause(String slug, LanguageType language);

    void incrementCommentCount(String slug, LanguageType language);

    /* =====================================================
       DASHBOARD (ADMIN / AUTHOR)
    ===================================================== */

    /**
     * Admin → all posts
     * Author → only own posts
     */
    List<Post> getPostsForDashboard(User currentUser);

    /* =====================================================
       ADMIN / AUTHOR EDIT
    ===================================================== */

    /**
     * Secure edit fetch
     */
    Post getPostForEdit(Long postId, User currentUser);

    Post updatePost(
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
    );

    /* =====================================================
       DELETE
    ===================================================== */

    void deletePost(Long postId, User currentUser);

    void togglePublishStatus(
            Long postId,
            boolean publish,
            User currentUser
    );
}
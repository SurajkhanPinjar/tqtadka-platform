package com.tqtadka.platform.service;

import com.tqtadka.platform.entity.*;

import java.util.List;

public interface PostService {

    /* =====================================================
       CREATE (ADMIN)
    ===================================================== */

    /**
     * Create a new post with sections.
     *
     * 🔒 RULE:
     * - Slug MUST be generated internally from title
     * - Slug MUST be unique across language
     * - Slug MUST NOT be null
     *
     * MUST persist:
     * - Post
     * - PostSection(s)
     */
    Post createPost(
            String title,
            String intro,
            CategoryType category,
            LanguageType language,
            String imageUrl,
            List<PostSection> sections,
            boolean publish
    );

    /* =====================================================
       PUBLIC READ (USER)
    ===================================================== */

    /**
     * Latest published posts by language
     */
    List<Post> getPublishedPosts(LanguageType language);

    /**
     * Category page posts
     */
    List<Post> getPostsByCategory(
            CategoryType category,
            LanguageType language
    );

    /**
     * Fetch ONE published post by slug + language
     *
     * ❌ slug must never be null
     */
    Post getPublishedPost(
            String slug,
            LanguageType language
    );

    /* =====================================================
       ENGAGEMENT (USER ACTIONS)
    ===================================================== */

    void incrementViews(String slug, LanguageType language);

    void addApplause(String slug, LanguageType language);

    void incrementCommentCount(String slug, LanguageType language);

    /* =====================================================
       ADMIN READ
    ===================================================== */

    List<Post> getAllPostsForAdmin();

    /**
     * Fetch post for edit (published or draft)
     */
    Post getPostForEdit(String slug, LanguageType language);

    /* =====================================================
       UPDATE (ADMIN)
    ===================================================== */

    /**
     * Update existing post.
     *
     * 🔒 RULE:
     * - Slug must NEVER change once created
     */
    Post updatePost(
            String slug,
            String title,
            String intro,
            CategoryType category,
            LanguageType language,
            String imageUrl,
            List<PostSection> sections,
            boolean publish
    );

    /* =====================================================
       DELETE
    ===================================================== */

    void deletePost(String slug, LanguageType language);

    void togglePublishStatus(
            String slug,
            LanguageType language,
            boolean publish
    );
}
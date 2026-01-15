package com.tqtadka.platform.service;

import com.tqtadka.platform.entity.*;

import java.util.List;

public interface PostService {

    /* =====================================================
       CREATE (ADMIN)
    ===================================================== */

    /**
     * Create a new post with sections.
     * MUST persist Post + PostSection together.
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

    List<Post> getPublishedPosts(LanguageType language);

    List<Post> getPostsByCategory(
            CategoryType category,
            LanguageType language
    );

    /**
     * Fetch published post WITH sections
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

    Post getPostForEdit(String slug, LanguageType language);

    /* =====================================================
       UPDATE (ADMIN)
    ===================================================== */

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
package com.tqtadka.platform.service;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.entity.Post;
import com.tqtadka.platform.entity.PostSection;

import java.util.List;

public interface PostService {

    /* =====================================================
       CREATE (ADMIN)
    ===================================================== */
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
     * Homepage / blog listing
     */
    List<Post> getPublishedPosts(LanguageType language);

    /**
     * Category based listing
     */
    List<Post> getPostsByCategory(
            CategoryType category,
            LanguageType language
    );

    /**
     * Blog detail page
     * ✅ Fetch published post WITH sections
     * ❌ No side effects
     */
    Post getPublishedPost(
            String slug,
            LanguageType language
    );

    /* =====================================================
       ENGAGEMENT (USER ACTIONS)
    ===================================================== */

    /**
     * 👁️ Increment view count (atomic)
     */
    void incrementViews(
            String slug,
            LanguageType language
    );

    /**
     * 👏 Applause (login required)
     */
    void addApplause(
            String slug,
            LanguageType language
    );

    /**
     * 💬 Increment comment counter
     */
    void incrementCommentCount(
            String slug,
            LanguageType language
    );

    /* =====================================================
       ADMIN READ
    ===================================================== */

    List<Post> getAllPostsForAdmin();

    Post getPostForEdit(
            String slug,
            LanguageType language
    );

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

    void deletePost(
            String slug,
            LanguageType language
    );
}
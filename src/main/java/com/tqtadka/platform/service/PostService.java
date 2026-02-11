package com.tqtadka.platform.service;

import com.tqtadka.platform.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

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
            String tagsInput, // 🔥 NEW (optional)
            List<String> slugs
    );

    /* =====================================================
       PUBLIC READ
    ===================================================== */

    List<Post> getPublishedPosts(LanguageType language);

    List<Post> getPostsByCategory(
            CategoryType category,
            LanguageType language
    );

    List<Post> getPostsByCategory(
            CategoryType category,
            LanguageType language,
            String sort
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
            String existingImageUrl,
            boolean removeImage,
            List<PostSection> sections,
            List<PostImageSection> imageSections, // ✅ ADD
            boolean publish,
            User user,
            AiPostMode aiPostMode,
            String[] promptNames,
            String[] promptTexts,
            String tags,
            List<String> relatedSlugs
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

    void addImageSection(
            Long postId,
            String heading,
            String description,
            String imageUrl,
            int order
    );

    public Post getPostForView(String slug, LanguageType language);


    public Post getPostForPublicView(String slug, LanguageType language);

    public List<Post> search(String rawQuery, LanguageType language);


    public Page<Post> getPostsByCategory(
            CategoryType category,
            LanguageType language,
            String sort,
            int page
    );

    public Page<Post> getPostsByCategory(
            CategoryType category,
            LanguageType language,
            String sort,
            int page,
            Boolean promptOnly
    );

    public List<Post> findAdminPosts(
            String q,
            LanguageType lang,
            CategoryType category,
            Long authorId,
            Boolean published,
            User currentUser
    );

    Post findByIdWithSections(Long id);

    public Post findFullPostForCopy(Long id);




}
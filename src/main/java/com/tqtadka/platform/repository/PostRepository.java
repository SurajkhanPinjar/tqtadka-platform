package com.tqtadka.platform.repository;

import com.tqtadka.platform.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /* =====================================================
       PUBLIC — SINGLE POST (WITH SECTIONS)
       ===================================================== */
    @Query("""
        select distinct p
        from Post p
        left join fetch p.sections
        where p.slug = :slug
          and p.language = :language
          and p.published = true
    """)
    Optional<Post> findPublishedPostWithSections(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );

    /* =====================================================
       PUBLIC — LISTING
       ===================================================== */
    List<Post> findByLanguageAndPublishedTrueOrderByPublishedAtDesc(
            LanguageType language
    );

    List<Post> findByCategoryAndLanguageAndPublishedTrueOrderByPublishedAtDesc(
            CategoryType category,
            LanguageType language
    );

    /* =====================================================
       ADMIN — ALL POSTS
       ===================================================== */
    List<Post> findAllByOrderByCreatedAtDesc();

    Optional<Post> findBySlugAndLanguage(
            String slug,
            LanguageType language
    );

    /* =====================================================
       AUTHOR — ONLY OWN POSTS
       ===================================================== */
    List<Post> findByCreatedByOrderByCreatedAtDesc(
            User createdBy
    );

    @Query("""
        select distinct p
        from Post p
        left join fetch p.sections
        where p.id = :postId
          and p.createdBy = :author
    """)
    Optional<Post> findForEditByAuthor(
            @Param("postId") Long postId,
            @Param("author") User author
    );

    /* =====================================================
       ADMIN — EDIT ANY POST
       ===================================================== */
    @Query("""
        select distinct p
        from Post p
        left join fetch p.sections
        where p.id = :postId
    """)
    Optional<Post> findForEditByAdmin(
            @Param("postId") Long postId
    );

    /* =====================================================
       UTIL
       ===================================================== */
    boolean existsBySlugAndLanguage(
            String slug,
            LanguageType language
    );

    /* =====================================================
       ENGAGEMENT — ATOMIC UPDATES
       ===================================================== */
    @Modifying
    @Query("""
        update Post p
        set p.views = p.views + 1
        where p.slug = :slug
          and p.language = :language
    """)
    void incrementViews(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );

    @Modifying
    @Query("""
        update Post p
        set p.applauseCount = p.applauseCount + 1
        where p.slug = :slug
          and p.language = :language
    """)
    void incrementApplause(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );

    @Modifying
    @Query("""
        update Post p
        set p.commentCount = p.commentCount + 1
        where p.slug = :slug
          and p.language = :language
    """)
    void incrementCommentCount(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );
}
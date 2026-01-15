package com.tqtadka.platform.repository;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.entity.Post;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    /* =====================================================
       SINGLE POST (PUBLIC – WITH SECTIONS)
       🔥 FIXED: DISTINCT avoids duplicate rows
    ===================================================== */
    @Query("""
        select distinct p from Post p
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
       PUBLIC LISTING (NO SECTIONS)
    ===================================================== */
    List<Post> findByLanguageAndPublishedTrueOrderByPublishedAtDesc(
            LanguageType language
    );

    List<Post> findByCategoryAndLanguageAndPublishedTrueOrderByPublishedAtDesc(
            CategoryType category,
            LanguageType language
    );

    /* =====================================================
       ADMIN
    ===================================================== */
    Optional<Post> findBySlugAndLanguage(
            String slug,
            LanguageType language
    );

    List<Post> findAllByOrderByCreatedAtDesc();

    /* =====================================================
       UTIL
    ===================================================== */
    boolean existsBySlugAndLanguage(
            String slug,
            LanguageType language
    );

    /* =====================================================
       🔥 ENGAGEMENT (ATOMIC & SCALABLE)
    ===================================================== */

    @Modifying(clearAutomatically = true)
    @Query("""
        update Post p
        set p.views = p.views + 1
        where p.slug = :slug and p.language = :language
    """)
    void incrementViews(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );

    @Modifying(clearAutomatically = true)
    @Query("""
        update Post p
        set p.applauseCount = p.applauseCount + 1
        where p.slug = :slug and p.language = :language
    """)
    void incrementApplause(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );

    @Modifying(clearAutomatically = true)
    @Query("""
        update Post p
        set p.commentCount = p.commentCount + 1
        where p.slug = :slug and p.language = :language
    """)
    void incrementCommentCount(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );
}
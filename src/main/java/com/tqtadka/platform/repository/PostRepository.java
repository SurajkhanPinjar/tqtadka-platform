package com.tqtadka.platform.repository;

import com.tqtadka.platform.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
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

    @EntityGraph(attributePaths = "aiPrompts")
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
    @EntityGraph(attributePaths = {"sections", "aiPrompts"})
    @Query("select p from Post p where p.id = :id")
    Optional<Post> findForEditByAdmin(@Param("id") Long id);

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

    @Query("""
    select distinct p
    from Post p
    left join fetch p.sections
    left join fetch p.aiPrompts
    where p.slug = :slug
      and p.language = :language
      and p.published = true
    """)
    Optional<Post> findPublishedPostWithAllRelations(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );

    @Query("""
    SELECT DISTINCT p
    FROM Post p
    LEFT JOIN FETCH p.sections
    LEFT JOIN FETCH p.aiPrompts
    WHERE p.slug = :slug
      AND p.language = :language
      AND p.published = true
""")
    Optional<Post> findPublishedPostWithSectionsAndPrompts(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );

    @Query("""
    select p from Post p
    left join fetch p.imageSections
    left join fetch p.aiPrompts
    where p.id = :id
""")
    Optional<Post> findByIdWithRelations(@Param("id") Long id);

    @Query("""
    select distinct p
    from Post p
    left join fetch p.sections s
    left join fetch p.imageSections i
    left join fetch p.aiPrompts ap
    where p.id = :postId
""")
    Optional<Post> findPostForEdit(
            @Param("postId") Long postId
    );

    @Query("""
    select distinct p
    from Post p
    left join fetch p.sections s
    left join fetch p.imageSections i
    left join fetch p.aiPrompts ap
    where p.slug = :slug
      and p.language = :language
      and p.published = true
""")
    Optional<Post> findPublishedPostForView(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );

    @Query("""
    select distinct p from Post p
    left join fetch p.imageSections
    left join fetch p.sections
    left join fetch p.aiPrompts
    where p.slug = :slug
      and p.language = :language
      and p.published = true
""")
    Optional<Post> findPostForPublicView(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );

    @Query("""
    select p.id as id,
           p.title as title,
           p.slug as slug,
           p.language as language,
           p.imageUrl as imageUrl,
           p.publishedAt as publishedAt
    from Post p
    where p.language = :language
      and p.published = true
    order by p.publishedAt desc
""")
    List<RecentPostView> findRecentPostsForSidebar(@Param("language") LanguageType language);
}
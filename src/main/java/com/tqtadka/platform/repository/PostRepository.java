package com.tqtadka.platform.repository;

import com.tqtadka.platform.dto.*;
import com.tqtadka.platform.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    left join fetch p.tags t
    where p.id = :postId
""")
    Optional<Post> findPostForEdit(@Param("postId") Long postId);

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

//    @Query("""
//    select distinct p from Post p
//    left join fetch p.imageSections
//    left join fetch p.sections
//    left join fetch p.aiPrompts
//    where p.slug = :slug
//      and p.language = :language
//      and p.published = true
//""")
//    Optional<Post> findPostForPublicView(
//            @Param("slug") String slug,
//            @Param("language") LanguageType language
//    );

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
select
    p.slug as slug,
    p.title as title,
    p.imageUrl as imageUrl,
    p.category as category,
    p.authorName as authorName,
    p.views as views,
    p.applauseCount as applauseCount
from Post p
where p.slug in :slugs
  and p.language = :language
  and p.published = true
""")
    List<RelatedPostView> findRelatedPostViews(
            @Param("slugs") Set<String> slugs,
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
//    @Query("""
//    select count(p)
//    from Post p
//    where p.published = true
//""")
//    long countPublishedPosts();

//    long countByAuthorNameAndPublishedTrue(String authorName);

//    @Query("""
//    select coalesce(sum(p.views), 0)
//    from Post p
//    where p.published = true
//      and (:author is null or p.authorName = :author)
//""")
//    Long totalViewsByAuthor(@Param("author") String author);

    @Query("""
    select coalesce(sum(p.views), 0)
    from Post p
    where p.published = true
      and p.publishedAt >= :start
      and (:author is null or p.authorName = :author)
""")
    Long viewsFromByAuthor(
            @Param("start") LocalDateTime start,
            @Param("author") String author
    );

    @Query("""
    select p
    from Post p
    where (:category is null or p.category = :category)
      and (:author is null or p.createdBy = :author)
    order by p.createdAt desc
""")
    List<Post> filterPosts(
            @Param("category") CategoryType category,
            @Param("author") User author
    );

    /* =====================
   DASHBOARD COUNTS
===================== */

    @Query("""
    select count(p)
    from Post p
    where p.published = true
""")
    long countPublishedPosts();

    long countByCreatedBy(User createdBy);

    @Query("""
    select coalesce(sum(p.views),0)
    from Post p
""")
    Long totalViews();

    @Query("""
    select coalesce(sum(p.views),0)
    from Post p
    where p.createdBy = :author
""")
    Long totalViewsByCreatedBy(@Param("author") User author);

    @Query("""
    select coalesce(sum(p.views),0)
    from Post p
    where p.publishedAt >= :start
""")
    Long viewsFrom(@Param("start") LocalDateTime start);

    @Query("""
    select coalesce(sum(p.views),0)
    from Post p
    where p.publishedAt >= :start
      and p.createdBy = :author
""")
    Long viewsFromByCreatedBy(
            @Param("start") LocalDateTime start,
            @Param("author") User author
    );


    @Query("""
    select p.id as id,
           p.title as title,
           p.category as category,
           p.publishedAt as publishedAt,
           p.views as views
    from Post p
    where (:category is null or p.category = :category)
      and (:author is null or p.createdBy = :author)
    order by p.publishedAt desc
""")
    List<DashboardPostView> filterDashboardPosts(
            @Param("category") CategoryType category,
            @Param("author") User author
    );

    @Query("""
select
    p.slug as slug,
    p.title as title,
    p.category as category,
    coalesce(p.authorName, u.name) as authorName,

    /* Month analytics from events (safe even if empty) */
    coalesce(sum(
        case when v.createdAt >= :monthStart then 1 else 0 end
    ), 0) as viewsThisMonth,

    coalesce(sum(
        case when v.createdAt >= :lastMonthStart
              and v.createdAt < :monthStart then 1 else 0 end
    ), 0) as viewsLastMonth,

    /* 🔥 TOTAL VIEWS FROM POST TABLE (CRITICAL FIX) */
    p.views as totalViews,

    p.publishedAt as publishedAt

from Post p
join p.createdBy u
left join p.viewEvents v

where (:category is null or p.category = :category)
  and (:author is null or p.createdBy = :author)
  and p.published = true

group by
    p.id,
    p.views,
    u.name,
    p.authorName,
    p.category,
    p.title,
    p.slug,
    p.publishedAt
""")
    List<DashboardPostView> findDashboardPosts(
            @Param("category") CategoryType category,
            @Param("author") User author,
            @Param("monthStart") LocalDateTime monthStart,
            @Param("lastMonthStart") LocalDateTime lastMonthStart,
            Sort sort
    );

    Optional<Post> findBySlugAndLanguageAndPublishedTrue(
            String slug,
            LanguageType language
    );


    List<Post> findByCategoryAndLanguageAndPublishedTrue(
            CategoryType category,
            LanguageType language,
            Sort sort
    );


    //Tags
    @Query("""
    select p from Post p
    left join fetch p.tags
    where p.slug = :slug
      and p.language = :language
      and p.published = true
""")
    Optional<Post> findPostForViewWithTags(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );


    @Query("""
    select distinct p from Post p
    left join fetch p.imageSections
    left join fetch p.sections
    left join fetch p.aiPrompts
    left join fetch p.tags
    where p.slug = :slug
      and p.language = :language
      and p.published = true
""")
    Optional<Post> findPostForView(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );

    @Query("""
    select distinct p
    from Post p
    left join fetch p.tags
    where p.id = :id
""")
    Optional<Post> findPostForEditWithTags(@Param("id") Long id);

    @Query("""
    select distinct p
    from Post p
    left join fetch p.sections
    left join fetch p.imageSections
    left join fetch p.tags
    where p.slug = :slug
      and p.language = :language
      and p.published = true
""")
    Optional<Post> findPublishedPostWithTags(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );

    @Query("""
    select distinct p
    from Post p
    join p.tags t
    where t.slug = :slug
      and p.language = :language
      and p.published = true
""")
    List<Post> findPublishedPostsByTag(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );

    List<Post> findBySlugInAndLanguageAndPublishedTrue(
            List<String> slugs,
            LanguageType language
    );

    @Query("""
select distinct p
from Post p
left join fetch p.tags
left join fetch p.sections
left join fetch p.imageSections
left join fetch p.aiPrompts
left join fetch p.relatedPostSlugs
where p.id = :id
""")
    Optional<Post> findPostForEditFull(@Param("id") Long id);


//    @Query("""
//    select
//        p.slug as slug,
//        p.title as title,
//        p.imageUrl as imageUrl,
//        p.category as category,
//        p.authorName as authorName,
//        p.views as views,
//        p.applauseCount as applauseCount
//    from Post p
//    where p.slug in :slugs
//      and p.language = :language
//      and p.published = true
//""")
//    List<RelatedPostView> findRelatedPostViews(
//            @Param("slugs") Set<String> slugs,
//            @Param("language") LanguageType language
//    );

    @Query("""
    select distinct p
    from Post p
    left join fetch p.tags
    where p.slug = :slug
      and p.language = :language
      and p.published = true
""")
    Optional<Post> findPostForPublicViewWithTags(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );

    @Query("""
select
  p.slug as slug,
  p.title as title,
  p.imageUrl as imageUrl,
  p.category as category,
  p.authorName as authorName,
  p.views as views,
  p.applauseCount as applauseCount
from Post p
where p.slug in :slugs
  and p.language = :language
  and p.published = true
""")
    Set<RelatedPostView> findRelatedPostViews(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );

    @Query("""
select
    p.slug as slug,
    p.title as title,
    p.imageUrl as imageUrl,
    p.language as language,
    p.publishedAt as publishedAt
from Post p
where p.published = true
  and p.language = :language
order by
    (p.views * 0.6 +
     p.applauseCount * 2 +
     p.commentCount * 1.5) desc
""")
    List<TrendingPostView> findTrendingPosts(
            @Param("language") LanguageType language,
            Pageable pageable
    );

    @Query("""
select
    p.id as id,
    p.slug as slug,
    p.title as title,
    p.imageUrl as imageUrl,
    p.language as language,
    p.publishedAt as publishedAt
from Post p
where p.category = :category
  and p.language = :language
  and p.published = true
  and p.slug <> :currentSlug
order by p.publishedAt desc
""")
    List<SidebarPostView> findYouMightLikePosts(
            @Param("category") CategoryType category,
            @Param("language") LanguageType language,
            @Param("currentSlug") String currentSlug,
            Pageable pageable
    );

    @Query("""
    select
        p.id as id,
        p.title as title,
        p.slug as slug,
        p.imageUrl as imageUrl,
        p.category as category,
        p.language as language,
        p.authorName as authorName,
        p.views as views,
        p.applauseCount as applauseCount,
        p.publishedAt as publishedAt
    from Post p
    where p.category = :category
      and p.published = true
    order by p.publishedAt desc
""")
    Page<PostCardView> findByCategory(
            @Param("category") CategoryType category,
            Pageable pageable
    );

    @Query("""
    select 
        p.slug as slug,
        p.title as title,
        p.imageUrl as imageUrl,
        p.views as views,
        p.applauseCount as applauseCount,
        p.authorName as authorName,
        p.category as category
    from Post p
    where p.published = true
    order by p.publishedAt desc
""")
    List<HomePostView> findLatestHomePosts(Pageable pageable);

    // 🔹 Latest Posts
    @Query("""
        select 
            p.slug as slug,
            p.title as title,
            p.imageUrl as imageUrl,
            p.category as category,
            p.views as views,
            p.applauseCount as applauseCount
        from Post p
        where p.language = :language
          and p.published = true
        order by p.publishedAt desc
    """)
    List<HomePostView> findLatestHomePosts(
            @Param("language") LanguageType language,
            Pageable pageable
    );

    // 🔹 Trending (views + engagement)
    @Query("""
        select 
            p.slug as slug,
            p.title as title,
            p.imageUrl as imageUrl,
            p.category as category,
            p.views as views,
            p.applauseCount as applauseCount
        from Post p
        where p.language = :language
          and p.published = true
        order by (p.views + p.applauseCount) desc
    """)
    List<HomePostView> findTrendingHomePosts(
            @Param("language") LanguageType language,
            Pageable pageable
    );

    // 🔥 Trending by category
    @Query("""
        select
            p.slug as slug,
            p.title as title,
            p.imageUrl as imageUrl,
            p.category as category,
            p.views as views,
            p.applauseCount as applauseCount
        from Post p
        where p.language = :language
          and p.published = true
          and p.category = :category
        order by (p.views + p.applauseCount) desc
    """)
    List<HomePostView> findTrendingByCategory(
            @Param("language") LanguageType language,
            @Param("category") CategoryType category,
            Pageable pageable
    );

    @Query("""
SELECT p FROM Post p
WHERE p.language = :lang
AND p.published = true
AND (
       LOWER(p.title) LIKE CONCAT('%', LOWER(:q), '%')
    OR LOWER(FUNCTION('CAST', p.intro, 'text')) LIKE CONCAT('%', LOWER(:q), '%')
)
ORDER BY p.publishedAt DESC
""")
    List<Post> searchByText(
            @Param("q") String q,
            @Param("lang") LanguageType lang,
            Pageable pageable
    );

    @Query("""
SELECT DISTINCT p FROM Post p
JOIN p.tags t
WHERE p.language = :lang
AND p.published = true
AND LOWER(t.name) LIKE CONCAT('%', LOWER(:q), '%')
ORDER BY p.publishedAt DESC
""")
    List<Post> searchByTags(
            @Param("q") String q,
            @Param("lang") LanguageType lang
    );

    @Query("""
SELECT p FROM Post p
WHERE p.language = :lang
AND p.published = true
AND LOWER(p.title) LIKE CONCAT('%', LOWER(:q), '%')
ORDER BY p.publishedAt DESC
""")
    List<Post> searchByTitle(
            @Param("q") String q,
            @Param("lang") LanguageType lang,
            Pageable pageable
    );

    @Query(
            value = """
    SELECT * FROM post p
    WHERE p.language = :lang
      AND p.published = true
      AND p.intro ILIKE CONCAT('%', :q, '%')
    ORDER BY p.published_at DESC
    """,
            nativeQuery = true
    )
    List<Post> searchByIntroNative(
            @Param("q") String q,
            @Param("lang") String lang
    );

    Page<Post> findByCategoryAndLanguageAndPublishedTrue(
            CategoryType category,
            LanguageType language,
            Pageable pageable
    );

    @Query("""
    SELECT p FROM Post p
    WHERE p.category = 'AI'
      AND p.language = :language
      AND p.published = true
      AND p.aiPostMode = 'PROMPT'
""")
    Page<Post> findAiPromptPosts(
            @Param("language") LanguageType language,
            Pageable pageable
    );

    @Query("""
    SELECT p FROM Post p
    WHERE (:q IS NULL OR LOWER(p.title) LIKE :q OR LOWER(p.slug) LIKE :q)
      AND (:lang IS NULL OR p.language = :lang)
      AND (:category IS NULL OR p.category = :category)
      AND (:published IS NULL OR p.published = :published)
      AND (:authorId IS NULL OR p.createdBy.id = :authorId)
      AND (:currentUserId IS NULL OR p.createdBy.id = :currentUserId)
    ORDER BY p.createdAt DESC
""")
    List<Post> searchAdminPosts(
            String q,
            LanguageType lang,
            CategoryType category,
            Long authorId,
            Boolean published,
            Long currentUserId
    );


}
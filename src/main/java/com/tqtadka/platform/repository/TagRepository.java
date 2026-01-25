package com.tqtadka.platform.repository;

import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.entity.Post;
import com.tqtadka.platform.entity.Tag;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("""
    select distinct p
    from Post p
    join fetch p.tags t
    where t.slug = :slug
      and p.language = :language
      and p.published = true
""")
    List<Post> findPublishedPostsByTag(
            @Param("slug") String slug,
            @Param("language") LanguageType language
    );


}
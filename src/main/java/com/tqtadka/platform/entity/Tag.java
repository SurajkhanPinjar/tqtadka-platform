package com.tqtadka.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "tag",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "slug")
        }
)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String name;   // AI, Startups, Career

    @Column(nullable = false, length = 60)
    private String slug;   // ai, startups, career

    /* =========================
       BACK REFERENCE
    ========================= */
    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new HashSet<>();

    // 🔥 FIX START
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag tag)) return false;
        return slug != null && slug.equals(tag.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug);
    }
}
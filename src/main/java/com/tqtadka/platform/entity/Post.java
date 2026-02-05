package com.tqtadka.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "post",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"slug", "language"})
        }
)
public class Post {

    /* =========================
       PRIMARY KEY
    ========================= */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =========================
       BASIC INFO
    ========================= */
    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 255)
    private String slug;

    @Column(length = 500)
    private String imageUrl;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String intro;

    /* =========================
       ENUMS
    ========================= */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CategoryType category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private LanguageType language;

    /* =========================
       STATUS
    ========================= */
    @Column(nullable = false)
    private boolean published;

    /* =========================
       ENGAGEMENT METRICS
    ========================= */
    @Builder.Default
    @Column(nullable = false)
    private long views = 0;

    @Builder.Default
    @Column(nullable = false)
    private long applauseCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private long commentCount = 0;

    /* =========================
       🔐 AUTHOR (CRITICAL FIX)
    ========================= */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /* =========================
       OPTIONAL DISPLAY NAME
       (NOT FOR SECURITY)
    ========================= */
    @Column(length = 120)
    private String authorName;

    /* =========================
       TIMESTAMPS
    ========================= */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    /* =========================
       POST SECTIONS
    ========================= */
    @Builder.Default
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<PostSection> sections = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_post_mode")
    private AiPostMode aiPostMode; // null for non-AI category

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<AiPrompt> aiPrompts = new HashSet<>();

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<PostImageSection> imageSections = new HashSet<>();

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PostViewEvent> viewEvents = new ArrayList<>();

    /* =========================
   TAGS
========================= */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

//    public Set<Tag> getTags() {
//        return tags == null
//                ? Collections.emptySet()
//                : tags.stream().filter(Objects::nonNull).collect(Collectors.toSet());
//    }
public Set<Tag> getTags() {
    return tags == null
            ? Collections.emptySet()
            : tags.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
}

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "post_related_slugs",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "related_slug", nullable = false)
    private Set<String> relatedPostSlugs = new HashSet<>();
}
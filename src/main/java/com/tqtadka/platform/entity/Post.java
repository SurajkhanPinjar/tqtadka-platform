package com.tqtadka.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
        },
        indexes = {

                // 🔥 Homepage (latest)
                @Index(
                        name = "idx_post_home_listing",
                        columnList = "published,deleted,language,published_at"
                ),

                // 🔥 Category latest
                @Index(
                        name = "idx_post_category_listing",
                        columnList = "published,deleted,category,language,published_at"
                ),

                // 🔥 Trending by category
                @Index(
                        name = "idx_post_trending",
                        columnList = "published,deleted,category,language,engagement_score"
                )
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

    @Column(length = 300)
    private String metaDescription;

    @Column(name = "engagement_score", nullable = false)
    private Long engagementScore = 0L;

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

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
        normalizeSlugInternal();
        recalculateEngagementScore();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        normalizeSlugInternal();
        recalculateEngagementScore();
    }

    private void normalizeSlugInternal() {
        if (slug != null) {
            slug = slug.trim().toLowerCase();
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

    @Column(name = "has_kannada_version", nullable = false)
    private boolean hasKannadaVersion = false;

    /* =========================
   TAGS
========================= */
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();


    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<FaqItem> faqs = new HashSet<>();


public Set<Tag> getTags() {
    return tags;
}

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "post_related_slugs",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "related_slug", nullable = false)
    private Set<String> relatedPostSlugs = new HashSet<>();

    @Column(name = "reading_time_minutes")
    private Integer readingTimeMinutes;

    private LocalDateTime updatedAt;

    @Column(name = "word_count")
    private Integer wordCount;

    @Column(nullable = false)
    private boolean deleted = false;

    public String getTimeAgo() {

        if (this.publishedAt == null) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();

        long minutes = ChronoUnit.MINUTES.between(publishedAt, now);
        long hours = ChronoUnit.HOURS.between(publishedAt, now);
        long days = ChronoUnit.DAYS.between(publishedAt, now);

        if (minutes < 1) {
            return "Just now";
        }

        if (minutes < 60) {
            return minutes + " min ago";
        }

        if (hours < 24) {
            return hours + " hr ago";
        }

        if (days < 7) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        }

        return publishedAt.format(
                DateTimeFormatter.ofPattern("dd MMM yyyy")
        );
    }

    public String getEffectiveMetaDescription() {

        if (metaDescription != null && !metaDescription.isBlank()) {
            return metaDescription;
        }

        if (intro != null && !intro.isBlank()) {
            return trimTo160(stripHtml(intro));
        }

        if (sections != null && !sections.isEmpty()) {
            for (PostSection section : sections) {
                if (section.getContent() != null && !section.getContent().isBlank()) {
                    return trimTo160(stripHtml(section.getContent()));
                }
            }
        }

        return "Structured AI insights, tools, and systems thinking for serious builders.";
    }

    private String stripHtml(String input) {
        return input == null ? "" :
                input.replaceAll("<[^>]*>", "")
                        .replaceAll("\\s+", " ")
                        .trim();
    }

    private String trimTo160(String input) {
        String clean = input.trim();
        return clean.length() > 160 ? clean.substring(0, 157) + "..." : clean;
    }

    public void recalculateEngagementScore() {
        this.engagementScore = this.views + this.applauseCount;
    }

}
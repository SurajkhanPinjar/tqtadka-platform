package com.tqtadka.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Column(nullable = false)
    private CategoryType category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LanguageType language;

    /* =========================
       STATUS
    ========================= */
    @Column(nullable = false)
    private boolean published;

    /* =========================
       ENGAGEMENT METRICS
    ========================= */

    // 👁️ total views
    @Builder.Default
    @Column(nullable = false)
    private long views = 0;

    // 👏 applause (not just like)
    @Builder.Default
    @Column(nullable = false)
    private long applauseCount = 0;

    // 💬 total comments
    @Builder.Default
    @Column(nullable = false)
    private long commentCount = 0;

    /* =========================
       AUTHOR INFO
    ========================= */

    @Column(length = 120)
    private String authorName;

    /* =========================
       TIMESTAMPS
    ========================= */

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // When post was published (important for blog sorting)
    private LocalDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /* =========================
       🟢 POST SECTIONS
    ========================= */
    @Builder.Default
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PostSection> sections = new ArrayList<>();
}
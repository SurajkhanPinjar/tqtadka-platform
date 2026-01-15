package com.tqtadka.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "comment",
        indexes = {
                @Index(name = "idx_comment_post", columnList = "post_id")
        }
)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "post")   // 🔥 avoid lazy-loading issues
public class Comment {

    /* =========================
       PRIMARY KEY
    ========================= */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /* =========================
       COMMENTER INFO
    ========================= */
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String email;   // ❌ not exposed in UI

    /* =========================
       COMMENT CONTENT
    ========================= */
    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /* =========================
       RELATIONSHIP
    ========================= */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /* =========================
       TIMESTAMP
    ========================= */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
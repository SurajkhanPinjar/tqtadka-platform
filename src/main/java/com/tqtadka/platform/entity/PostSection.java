package com.tqtadka.platform.entity;

import com.tqtadka.platform.entity.Post;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "post_section",
        indexes = {
                @Index(name = "idx_post_section_post", columnList = "post_id")
        }
)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "post")
public class PostSection {

    /* =========================
       PRIMARY KEY
    ========================= */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /* =========================
       SECTION CONTENT
    ========================= */

    @Column(length = 255)
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(length = 500)
    private String imageUrl;

    /* =========================
       BULLET BLOCK
    ========================= */

    @Column(length = 255)
    private String bulletTitle;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String bullets;

    /* =========================
       TIP / SOLUTION BLOCK
    ========================= */

    @Column(length = 255)
    private String tipTitle;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String tipContent;

    /* =========================
       RELATIONSHIP
    ========================= */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
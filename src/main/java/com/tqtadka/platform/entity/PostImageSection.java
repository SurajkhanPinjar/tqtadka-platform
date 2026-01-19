package com.tqtadka.platform.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "post_image_section")
public class PostImageSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =========================
       PARENT POST
    ========================= */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /* =========================
       CONTENT
    ========================= */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(length = 200)
    private String heading;

    @Column(columnDefinition = "TEXT")
    private String description;

    /* =========================
       ORDER
    ========================= */
    @Column(name = "display_order", nullable = false)
    private int displayOrder;
}
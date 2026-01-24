package com.tqtadka.platform.entity;

import com.tqtadka.platform.entity.Post;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_views")
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // optional (viewer)

    @Column(nullable = false)
    private LocalDateTime viewedAt;

    // getters & setters
}
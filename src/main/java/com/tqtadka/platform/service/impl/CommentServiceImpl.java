package com.tqtadka.platform.service.impl;

import com.tqtadka.platform.entity.Comment;
import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.entity.Post;
import com.tqtadka.platform.repository.CommentRepository;
import com.tqtadka.platform.repository.PostRepository;
import com.tqtadka.platform.service.CommentService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            PostRepository postRepository
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    /* =====================================
       READ COMMENTS FOR A POST (PUBLIC)
    ===================================== */
    @Override
    public List<Comment> getCommentsForPost(
            String slug,
            LanguageType language
    ) {

        Post post = postRepository
                .findPublishedPostWithSections(slug, language)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return commentRepository.findByPostOrderByCreatedAtDesc(post);
    }

    /* =====================================
       ADD COMMENT (PUBLIC)
    ===================================== */
    @Override
    public void addComment(
            String slug,
            LanguageType language,
            String name,
            String email,
            String content
    ) {

        if (isBlank(name) || isBlank(email) || isBlank(content)) {
            throw new IllegalArgumentException("Invalid comment data");
        }

        Post post = postRepository
                .findPublishedPostWithSections(slug, language)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .name(name.trim())
                .email(email.trim())
                .content(content.trim())
                .post(post)
                .build();

        commentRepository.save(comment);

        // 🔥 atomic DB update (NO entity reload)
        postRepository.incrementCommentCount(slug, language);
    }

    /* =====================================
       UTIL
    ===================================== */
    private boolean isBlank(String v) {
        return v == null || v.isBlank();
    }
}
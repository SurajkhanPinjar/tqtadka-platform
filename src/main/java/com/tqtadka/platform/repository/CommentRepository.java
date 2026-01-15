package com.tqtadka.platform.repository;

import com.tqtadka.platform.entity.Comment;
import com.tqtadka.platform.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostOrderByCreatedAtDesc(Post post);
}
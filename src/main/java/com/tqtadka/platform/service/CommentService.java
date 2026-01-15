package com.tqtadka.platform.service;

import com.tqtadka.platform.entity.Comment;
import com.tqtadka.platform.entity.LanguageType;

import java.util.List;

public interface CommentService {

    /* =====================================
       READ COMMENTS FOR A POST
    ===================================== */
    List<Comment> getCommentsForPost(
            String slug,
            LanguageType language
    );

    /* =====================================
       ADD COMMENT
    ===================================== */
    void addComment(
            String slug,
            LanguageType language,
            String name,
            String email,
            String content
    );
}
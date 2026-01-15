package com.tqtadka.platform.controller;

import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/comment/add")
    public String addComment(
            @RequestParam String slug,
            @RequestParam LanguageType language,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String content
    ) {

        // ✅ Single responsibility
        commentService.addComment(
                slug,
                language,
                name,
                email,
                content
        );

        return "redirect:/" + language.name().toLowerCase() + "/blog/" + slug;
    }
}
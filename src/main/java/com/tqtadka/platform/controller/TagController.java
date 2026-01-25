package com.tqtadka.platform.controller;

import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.entity.Post;
import com.tqtadka.platform.entity.Tag;
import com.tqtadka.platform.service.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/{lang}/tag/{slug}")
    public String postsByTag(
            @PathVariable String lang,
            @PathVariable String slug,
            Model model
    ) {
        LanguageType language =
                "kn".equalsIgnoreCase(lang)
                        ? LanguageType.KN
                        : LanguageType.EN;

        Tag tag = tagService.getBySlug(slug);
        List<Post> posts = tagService.getPublishedPostsByTag(slug, language);

        model.addAttribute("tag", tag);
        model.addAttribute("posts", posts);
        model.addAttribute("lang", lang.toLowerCase());

        return "blog/tag-posts";
    }
}
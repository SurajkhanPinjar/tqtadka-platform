package com.tqtadka.platform.controller;

import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.entity.Post;
import com.tqtadka.platform.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {

    private final PostService postService;

    public SearchController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{lang:en|kn}/search")
    public String search(
            @PathVariable String lang,
            @RequestParam("q") String q,
            Model model
    ) {
        LanguageType language =
                "kn".equalsIgnoreCase(lang)
                        ? LanguageType.KN
                        : LanguageType.EN;

        List<Post> results = postService.search(q, language);

        model.addAttribute("lang", lang);
        model.addAttribute("query", q);
        model.addAttribute("results", results);

        return "search";
    }
}
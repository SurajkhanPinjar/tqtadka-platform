package com.tqtadka.platform.controller;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    private final PostService postService;

    public HomeController(PostService postService) {
        this.postService = postService;
    }

    /* =====================================================
       HOME (LANGUAGE AWARE)
    ===================================================== */

    @GetMapping("/")
    public String homeDefault(Model model) {
        return homeByLanguage("en", model);
    }

    @GetMapping("/{lang:en|kn}")
    public String homeByLanguage(@PathVariable String lang,
                                 Model model) {

        LanguageType language =
                "kn".equalsIgnoreCase(lang)
                        ? LanguageType.KN
                        : LanguageType.EN;

        // ✅ REQUIRED for header fragment
        model.addAttribute("lang", lang);
        model.addAttribute("categories", CategoryType.values());
        model.addAttribute("activeCategory", null);

        // ✅ Language-specific posts
        model.addAttribute(
                "posts",
                postService.getPublishedPosts(language)
        );

        return "home";
    }
}
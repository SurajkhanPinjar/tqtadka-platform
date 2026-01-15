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
       HOME DEFAULT → ENGLISH
       /
    ===================================================== */
    @GetMapping("/")
    public String homeDefault(Model model) {
        return homeByLanguage("en", model);
    }

    /* =====================================================
       HOME (LANGUAGE AWARE)
       /en
       /kn
    ===================================================== */
    @GetMapping("/{lang:en|kn}")
    public String homeByLanguage(
            @PathVariable String lang,
            Model model
    ) {

        // ✅ Normalize language
        String normalizedLang = lang.toLowerCase();

        LanguageType language =
                "kn".equals(normalizedLang)
                        ? LanguageType.KN
                        : LanguageType.EN;

        // ✅ Required for header + routing
        model.addAttribute("lang", normalizedLang);
        model.addAttribute("categories", CategoryType.values());
        model.addAttribute("activeCategory", null);

        // ✅ Load language-specific posts
        model.addAttribute(
                "posts",
                postService.getPublishedPosts(language)
        );

        return "home";
    }
}
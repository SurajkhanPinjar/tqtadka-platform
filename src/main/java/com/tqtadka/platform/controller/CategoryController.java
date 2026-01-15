package com.tqtadka.platform.controller;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CategoryController {

    private final PostService postService;

    public CategoryController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{lang}/category/{category}")
    public String viewByCategory(@PathVariable String lang,
                                 @PathVariable CategoryType category,
                                 Model model) {

        // ✅ SAFE language resolution
        LanguageType language =
                "kn".equalsIgnoreCase(lang)
                        ? LanguageType.KN
                        : LanguageType.EN;

        // ✅ REQUIRED for header fragment
        model.addAttribute("lang", lang);
        model.addAttribute("categories", CategoryType.values());
        model.addAttribute("activeCategory", category);

        // ✅ Language-aware posts
        model.addAttribute(
                "posts",
                postService.getPostsByCategory(category, language)
        );

        return "category";
    }
}
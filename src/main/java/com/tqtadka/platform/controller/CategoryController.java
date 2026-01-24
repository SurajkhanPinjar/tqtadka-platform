package com.tqtadka.platform.controller;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CategoryController {

    private final PostService postService;

    public CategoryController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{lang}/category/{category}")
    public String viewCategory(
            @PathVariable String lang,
            @PathVariable CategoryType category,

            // 🟢 NEW (SAFE)
            @RequestParam(defaultValue = "latest") String sort,

            Model model
    ) {

        LanguageType language =
                "kn".equalsIgnoreCase(lang)
                        ? LanguageType.KN
                        : LanguageType.EN;

        // 🔴 REQUIRED — header depends on these
        model.addAttribute("lang", lang);
        model.addAttribute("categories", CategoryType.values());
        model.addAttribute("activeCategory", category);

        // 🟢 pass sort to UI (for active button highlight)
        model.addAttribute("sort", sort);

        // 🟢 SORT-AWARE fetch (fallbacks inside service)
        model.addAttribute(
                "posts",
                postService.getPostsByCategory(category, language, sort)
        );

        return "category";
    }
}
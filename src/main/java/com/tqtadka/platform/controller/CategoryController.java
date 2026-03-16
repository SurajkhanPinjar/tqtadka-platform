package com.tqtadka.platform.controller;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.entity.Post;
import com.tqtadka.platform.service.PostService;
import org.springframework.data.domain.Page;
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

    @GetMapping("/{lang}/{categorySlug:ai|social-media|jobs|career|schemes}")
    public String viewCategory(
            @PathVariable String lang,
            @PathVariable String categorySlug,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Boolean prompt,
            Model model
    ){
        LanguageType language =
                "kn".equalsIgnoreCase(lang)
                        ? LanguageType.KN
                        : LanguageType.EN;

        CategoryType category = fromSlug(categorySlug);

        Page<Post> postPage =
                postService.getPostsByCategory(
                        category, language, sort, page, prompt
                );

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());

        model.addAttribute("sort", sort);
        model.addAttribute("prompt", prompt);

        model.addAttribute("lang", lang);
        model.addAttribute("categories", CategoryType.values());
        model.addAttribute("activeCategory", category);

        // =========================
        // BREADCRUMB
        // =========================
        model.addAttribute("categoryName", category.getDisplayName());
        model.addAttribute("categorySlug", category.getSlug());

        model.addAttribute("pageSize", postPage.getSize());

        // =====================
        // SCHEMA GENERATION
        // =====================

        String itemListJson =
                postService.buildItemListSchema(
                        postPage.getContent(),
                        lang,
                        page,
                        postPage.getSize()
                );

        String breadcrumbJson =
                postService.buildCategoryBreadcrumbSchema(
                        lang,
                        category
                );

        model.addAttribute("itemListJson", itemListJson);
        model.addAttribute("breadcrumbJson", breadcrumbJson);

        return "category";
    }

    private CategoryType fromSlug(String slug) {
        for (CategoryType c : CategoryType.values()) {
            if (c.getSlug().equalsIgnoreCase(slug)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Invalid category slug: " + slug);
    }
}
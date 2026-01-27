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

    @GetMapping("/{lang}/category/{category}")
    public String viewCategory(
            @PathVariable String lang,
            @PathVariable CategoryType category,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        LanguageType language =
                "kn".equalsIgnoreCase(lang)
                        ? LanguageType.KN
                        : LanguageType.EN;

        Page<Post> postPage =
                postService.getPostsByCategory(category, language, sort, page);

        model.addAttribute("lang", lang);
        model.addAttribute("categories", CategoryType.values());
        model.addAttribute("activeCategory", category);
        model.addAttribute("sort", sort);

        // 🔥 pagination data
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());

        return "category";
    }
}
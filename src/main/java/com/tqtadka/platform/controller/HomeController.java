package com.tqtadka.platform.controller;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.LanguageType;
import com.tqtadka.platform.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PostRepository postRepository;

    @GetMapping("/")
    public String homeDefault(Model model) {
        return homeByLanguage("en", model);
    }

    @GetMapping("/{lang:en|kn}")
    public String homeByLanguage(
            @PathVariable String lang,
            Model model
    ) {

        LanguageType language =
                "kn".equalsIgnoreCase(lang)
                        ? LanguageType.KN
                        : LanguageType.EN;

        model.addAttribute("lang", lang);

        // 🔹 Latest posts (top grid)
        model.addAttribute(
                "posts",
                postRepository.findLatestHomePosts(
                        language,
                        PageRequest.of(0, 9)
                )
        );

        // 🔥 Trending by category
        model.addAttribute("trendingAI",
                postRepository.findTrendingByCategory(
                        language, CategoryType.AI, PageRequest.of(0, 10)));

        model.addAttribute("trendingTech",
                postRepository.findTrendingByCategory(
                        language, CategoryType.TECH, PageRequest.of(0, 10)));

        model.addAttribute("trendingMoney",
                postRepository.findTrendingByCategory(
                        language, CategoryType.MONEY_AND_BUSINESS, PageRequest.of(0, 10)));

        model.addAttribute("trendingSkin",
                postRepository.findTrendingByCategory(
                        language, CategoryType.SKIN_HEALTH, PageRequest.of(0, 10)));

        model.addAttribute("trendingBeauty",
                postRepository.findTrendingByCategory(
                        language, CategoryType.BEAUTY_AND_STYLE, PageRequest.of(0, 10)));

        return "home";
    }
}
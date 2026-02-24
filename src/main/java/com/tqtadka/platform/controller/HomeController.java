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
        model.addAttribute("learnAI",
                postRepository.findTrendingByCategory(
                        language, CategoryType.LEARN_AI, PageRequest.of(0, 10)));

        model.addAttribute("aiTools",
                postRepository.findTrendingByCategory(
                        language, CategoryType.AI_TOOLS, PageRequest.of(0, 10)));

        model.addAttribute("aiAtWork",
                postRepository.findTrendingByCategory(
                        language, CategoryType.AI_AT_WORK, PageRequest.of(0, 10)));

        model.addAttribute("aiByIndustry",
                postRepository.findTrendingByCategory(
                        language, CategoryType.AI_BY_INDUSTRY, PageRequest.of(0, 10)));


        model.addAttribute("aiFuture",
                postRepository.findTrendingByCategory(
                        language, CategoryType.AI_FUTURE, PageRequest.of(0, 10)));

        model.addAttribute("aiNews",
                postRepository.findTrendingByCategory(
                        language, CategoryType.AI_NEWS, PageRequest.of(0, 10)));

        return "home";
    }
}
package com.tqtadka.platform.controller;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.Role;
import com.tqtadka.platform.entity.User;
import com.tqtadka.platform.repository.UserRepository;
import com.tqtadka.platform.security.CustomUserDetails;
import com.tqtadka.platform.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    @GetMapping
    public String dashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) CategoryType category,
            @RequestParam(required = false) Long authorId,
            @RequestParam(defaultValue = "publishedAt") String sort,
            @RequestParam(defaultValue = "desc") String dir,
            Model model
    ) {
        User user = userDetails.getUser();
        boolean isAdmin = user.getRole() == Role.ADMIN;

    /* =========================
       SAFETY: SORT + DIR
    ========================== */
        List<String> allowedSorts = List.of(
                "publishedAt",
                "viewsMonth",
                "totalViews",
                "title"
        );
        if (!allowedSorts.contains(sort)) {
            sort = "publishedAt";
        }

        if (!dir.equalsIgnoreCase("asc") && !dir.equalsIgnoreCase("desc")) {
            dir = "desc";
        }

    /* =========================
       AUTHOR FILTER RULE
    ========================== */
        // Non-admins must NEVER filter by other authors
        if (!isAdmin) {
            authorId = null;
        }

    /* =========================
       MODEL ATTRIBUTES
    ========================== */
        model.addAttribute("isAdmin", isAdmin);

        model.addAttribute("stats",
                dashboardService.getStats(user));

        model.addAttribute("posts",
                dashboardService.filterPosts(
                        user,
                        category,
                        authorId,
                        sort,
                        dir
                ));

        model.addAttribute("categories", CategoryType.values());

        if (isAdmin) {
            model.addAttribute("authors",
                    userRepository.findAllAuthors());
        }

        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedAuthorId", authorId);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("lang", "en");

        return "admin/dashboard";
    }
}
package com.tqtadka.platform.admin;

import com.tqtadka.platform.entity.CategoryType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String adminHome(Model model) {

        // Admin pages default to English
        model.addAttribute("lang", "en");

        // Needed for header fragment (safe even if not shown)
        model.addAttribute("categories", CategoryType.values());

        return "admin/home";
    }
}
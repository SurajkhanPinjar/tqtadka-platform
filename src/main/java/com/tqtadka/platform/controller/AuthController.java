package com.tqtadka.platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AuthController {

    @GetMapping({"/login", "/{lang}/login"})
    public String login(@PathVariable(required = false) String lang, Model model) {
        model.addAttribute("lang", lang != null ? lang : "en");
        return "auth/login";
    }

    @GetMapping({"/signup", "/{lang}/signup"})
    public String signup(@PathVariable(required = false) String lang, Model model) {
        model.addAttribute("lang", lang != null ? lang : "en");
        return "auth/signup";
    }
}
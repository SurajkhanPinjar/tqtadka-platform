package com.tqtadka.platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/{lang}")
public class PageController {

    @GetMapping("/about")
    public String about(@PathVariable String lang) {
        return "pages/about";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy(@PathVariable String lang) {
        return "pages/privacy-policy";
    }

    @GetMapping("/terms-and-conditions")
    public String termsAndConditions(@PathVariable String lang) {
        return "pages/terms-and-conditions";
    }

    @GetMapping("/disclaimer")
    public String disclaimer(@PathVariable String lang) {
        return "pages/disclaimer";
    }

    @GetMapping("/contact")
    public String contact(@PathVariable String lang) {
        return "pages/contact";
    }
}
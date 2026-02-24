package com.tqtadka.platform.admin;

import com.tqtadka.platform.dto.PromptRequest;
import com.tqtadka.platform.service.PromptService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;

    // 🔹 Show Page
    @GetMapping("/prompt-generator")
    public String showPage(Model model, HttpServletRequest request) {
        model.addAttribute("promptRequest", new PromptRequest());
        model.addAttribute("currentPath", request.getRequestURI());
        return "admin/admin-prompt-generator";
    }

    // 🔹 Generate Prompt
    @PostMapping("/prompt-generator")
    public String generatePrompt(
            @ModelAttribute PromptRequest promptRequest,
            Model model,
            HttpServletRequest request
    ) {

        String generatedPrompt = promptService.buildPrompt(promptRequest);

        model.addAttribute("generatedPrompt", generatedPrompt);
        model.addAttribute("promptRequest", promptRequest);
        model.addAttribute("currentPath", request.getRequestURI());

        return "admin/admin-prompt-generator";
    }
}
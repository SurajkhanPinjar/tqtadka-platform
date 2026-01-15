package com.tqtadka.platform.config;

import com.tqtadka.platform.entity.CategoryType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("categories")
    public CategoryType[] categories() {
        return CategoryType.values();
    }
}
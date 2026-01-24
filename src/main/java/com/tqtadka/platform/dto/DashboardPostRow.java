package com.tqtadka.platform.dto;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.LanguageType;

import java.time.LocalDate;

public record DashboardPostRow(
        Long id,
        String title,
        String slug,
        LanguageType language,
        String author,
        CategoryType category,
        long viewsToday,
        long viewsThisMonth,
        long viewsLastMonth,
        long totalViews,
        LocalDate publishedAt
) {}
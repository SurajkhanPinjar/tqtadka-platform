package com.tqtadka.platform.dto;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.LanguageType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface DashboardPostView {

    Long getId();
    String getTitle();
    String getSlug();
    LanguageType getLanguage();

    String getAuthorName();
    CategoryType getCategory();

    Long getViewsToday();
    Long getViewsThisMonth();
    Long getViewsLastMonth();
    Long getTotalViews();

    LocalDateTime getPublishedAt();
}
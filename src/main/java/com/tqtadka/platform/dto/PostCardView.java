package com.tqtadka.platform.dto;

import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.LanguageType;

import java.time.LocalDateTime;

public interface PostCardView {

    Long getId();
    String getTitle();
    String getSlug();
    String getImageUrl();

    CategoryType getCategory();   // 🔥 REQUIRED
    LanguageType getLanguage();   // 🔥 REQUIRED

    String getAuthorName();
    long getViews();
    long getApplauseCount();
    LocalDateTime getPublishedAt();
}
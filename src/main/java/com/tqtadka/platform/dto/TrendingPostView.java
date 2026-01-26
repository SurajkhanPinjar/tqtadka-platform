package com.tqtadka.platform.dto;

import com.tqtadka.platform.entity.LanguageType;

public interface TrendingPostView {
    String getSlug();
    String getTitle();
    String getImageUrl();
    LanguageType getLanguage();
    long getViews();
    long getApplauseCount();
    long getCommentCount();
}
package com.tqtadka.platform.dto;

import com.tqtadka.platform.entity.LanguageType;

public interface SidebarPostView {

    Long getId();
    String getSlug();
    String getTitle();
    String getImageUrl();
    long getViews();
    long getApplauseCount();
    LanguageType getLanguage();
}
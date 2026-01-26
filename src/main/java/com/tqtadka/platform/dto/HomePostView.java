package com.tqtadka.platform.dto;

public interface HomePostView {

    String getSlug();
    String getTitle();
    String getImageUrl();
    Long getViews();
    Long getApplauseCount();
    String getAuthorName();

    // 🔥 THIS IS THE KEY
    String getCategory();
}
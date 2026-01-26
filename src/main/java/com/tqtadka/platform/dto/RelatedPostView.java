package com.tqtadka.platform.dto;

import com.tqtadka.platform.entity.CategoryType;

public interface RelatedPostView {

    String getSlug();
    String getTitle();
    String getImageUrl();
    CategoryType getCategory();
    String getAuthorName();
    long getViews();
    long getApplauseCount();
}
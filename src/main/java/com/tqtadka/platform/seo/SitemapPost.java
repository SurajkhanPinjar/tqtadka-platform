package com.tqtadka.platform.seo;

import com.tqtadka.platform.entity.CategoryType;

import java.time.LocalDateTime;

public interface SitemapPost {

    String getSlug();

    CategoryType getCategory();

    LocalDateTime getPublishedAt();
}
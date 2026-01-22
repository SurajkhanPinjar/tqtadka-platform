package com.tqtadka.platform.entity;

public interface RecentPostView {
    Long getId();
    String getTitle();
    String getSlug();
    LanguageType getLanguage();
    String getImageUrl();
    java.time.LocalDateTime getPublishedAt();
}
package com.tqtadka.platform.entity;

public enum CategoryType {
    AI("AI", "ai"),
    SOCIAL_MEDIA("Social Media", "social-media"),
    JOBS("Jobs", "jobs"),
    CAREER("Career", "career"),
    SCHEMES("Schemes", "schemes");


    private final String displayName;
    private final String slug;

    CategoryType(String displayName, String slug) {
        this.displayName = displayName;
        this.slug = slug;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSlug() {
        return slug;
    }
}
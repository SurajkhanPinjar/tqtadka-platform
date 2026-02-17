package com.tqtadka.platform.entity;

public enum CategoryType {

    LEARN_AI("Learn AI", "learn-ai"),
    AI_TOOLS("AI Tools", "ai-tools"),
    AI_AT_WORK("AI at Work", "ai-at-work"),
    AI_BY_INDUSTRY("AI by Industry", "ai-by-industry"),
    AI_FUTURE("AI Future", "ai-future"),
    AI_NEWS("AI News", "ai-news");

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
package com.tqtadka.platform.entity;

public enum CategoryType {
    LEARN_AI("Learn AI"),
    AI_TOOLS("AI Tools"),
    AI_AT_WORK("AI at Work"),
    AI_BY_INDUSTRY("AI by Industry"),
    AI_FUTURE("AI Future"),
    AI_NEWS("AI News");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
package com.tqtadka.platform.entity;

public enum CategoryType {

    // 🔥 Core Authority Pillars (High RPM + SaaS Alignment)

    AI("AI"),
    TECH("Tech"),
    FINANCE("Finance"),
    CAREER("Career"),
    STUDENTS("Students");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
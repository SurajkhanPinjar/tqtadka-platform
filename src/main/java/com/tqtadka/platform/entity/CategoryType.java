package com.tqtadka.platform.entity;

public enum CategoryType {

    // 🔥 Tier 1 – Highest RPM / Advertiser Demand
    AI("AI"),
    TECH("Tech"),

    // 🟢 Tier 2 – Strong Wellness + Beauty Brands
    SOCIAL_MEDIA("Social Media"),
    JOBS("Jobs"),
    LIFE("Life"),
    MONEY_AND_BUSINESS("Money & Business"),

    SKIN_HEALTH("Skin & Health"),
    BEAUTY_AND_STYLE("Beauty & Style"),
    FITNESS("Fitness"),

    // 🟡 Tier 3 – Good Volume, Moderate RPM
    TRAVEL("Travel"),
    FOOD("Food"),

    // 🔵 Tier 4 – Engagement Driven
//    ENTERTAINMENT("Entertainment"),
//    SPORTS("Sports"),

    // ⚪ Tier 5 – Platform / Utility Content
    EDUCATION("Education"),
    BLOG("Blog");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
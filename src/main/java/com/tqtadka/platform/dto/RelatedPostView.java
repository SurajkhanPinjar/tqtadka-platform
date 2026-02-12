package com.tqtadka.platform.dto;

import com.tqtadka.platform.entity.CategoryType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public interface RelatedPostView {

    String getSlug();
    String getTitle();
    String getImageUrl();
    CategoryType getCategory();
    String getAuthorName();
    long getViews();
    long getApplauseCount();

    Integer getReadingTimeMinutes();

    LocalDateTime getPublishedAt();   // 🔥 add this

    default String getTimeAgo() {

        if (getPublishedAt() == null) return "";

        LocalDateTime now = LocalDateTime.now();

        long minutes = ChronoUnit.MINUTES.between(getPublishedAt(), now);
        long hours = ChronoUnit.HOURS.between(getPublishedAt(), now);
        long days = ChronoUnit.DAYS.between(getPublishedAt(), now);

        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " min ago";
        if (hours < 24) return hours + " hr ago";
        if (days < 7) return days + " day" + (days > 1 ? "s" : "") + " ago";

        return getPublishedAt().format(
                DateTimeFormatter.ofPattern("dd MMM yyyy")
        );
    }
}
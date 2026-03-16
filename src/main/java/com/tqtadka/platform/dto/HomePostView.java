package com.tqtadka.platform.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public interface HomePostView {

    String getSlug();
    String getTitle();
    String getImageUrl();
    Long getViews();
    Long getApplauseCount();
    String getAuthorName();
    String getCategory();
    Integer getReadingTimeMinutes();

    LocalDateTime getCreatedAt();

    default String getTimeAgo() {

        if (getCreatedAt() == null) return "";

        LocalDateTime now = LocalDateTime.now();

        long minutes = ChronoUnit.MINUTES.between(getCreatedAt(), now);
        long hours = ChronoUnit.HOURS.between(getCreatedAt(), now);
        long days = ChronoUnit.DAYS.between(getCreatedAt(), now);

        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " min ago";
        if (hours < 24) return hours + " hr ago";
        if (days < 7) return days + " day" + (days > 1 ? "s" : "") + " ago";

        return getCreatedAt().format(
                DateTimeFormatter.ofPattern("dd MMM yyyy")
        );
    }
}
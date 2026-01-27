package com.tqtadka.platform.dto;

import java.time.LocalDateTime;

interface HomePostProjection {
    String getTitle();
    String getSlug();
    String getImageUrl();
    Long getViews();
    LocalDateTime getCreatedAt();
}
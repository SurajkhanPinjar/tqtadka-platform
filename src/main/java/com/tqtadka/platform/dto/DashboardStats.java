package com.tqtadka.platform.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStats {

    private long totalPosts;

    private long totalViews;

    private long viewsThisMonth;

    private long viewsThisWeek;

    private long viewsToday;
}
package com.tqtadka.platform.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeAgoUtil {

    public static String timeAgo(LocalDateTime time) {
        if (time == null) return "";

        long days = ChronoUnit.DAYS.between(time, LocalDateTime.now());

        if (days < 1) {
            return "Today";
        }
        if (days < 7) {
            return days + (days == 1 ? " day ago" : " days ago");
        }
        if (days < 30) {
            long weeks = days / 7;
            return weeks + (weeks == 1 ? " week ago" : " weeks ago");
        }
        if (days < 365) {
            long months = days / 30;
            return months + (months == 1 ? " month ago" : " months ago");
        }

        long years = days / 365;
        return years + (years == 1 ? " year ago" : " years ago");
    }
}
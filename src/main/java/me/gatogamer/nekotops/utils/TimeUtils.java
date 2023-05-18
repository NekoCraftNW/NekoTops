package me.gatogamer.nekotops.utils;

import java.time.Duration;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
public class TimeUtils {
    public static String formatTime(final Duration duration) {
        final StringBuilder builder = new StringBuilder();

        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;
        days %= 7;

        builder.insert(0, seconds + "s");

        if (minutes > 0) {
            if (builder.length() > 0) {
                builder.insert(0, ' ');
            }
            builder.insert(0, minutes + "m");
        }
        if (hours > 0) {
            if (builder.length() > 0) {
                builder.insert(0, ' ');
            }
            builder.insert(0, hours + "h");
        }
        if (days > 0) {
            if (builder.length() > 0) {
                builder.insert(0, ' ');
            }
            builder.insert(0, days + "d");
        }
        if (weeks > 0) {
            if (builder.length() > 0) {
                builder.insert(0, ' ');
            }
            builder.insert(0, weeks + "w");
        }
        return builder.toString();
    }
}
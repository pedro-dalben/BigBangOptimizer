package com.pedrodalben.bigbangoptimizer.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeFormatters {
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .withZone(ZoneId.of("UTC"));

    public static String formatTimestamp(Instant instant) {
        return TIMESTAMP_FORMAT.format(instant);
    }

    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        if (seconds < 60) return seconds + "s";
        if (seconds < 3600) return (seconds / 60) + "m " + (seconds % 60) + "s";
        return (seconds / 3600) + "h " + ((seconds % 3600) / 60) + "m";
    }

    public static String formatDurationMs(long ms) {
        if (ms < 1000) return ms + "ms";
        if (ms < 60000) return String.format("%.1fs", ms / 1000.0);
        return formatDuration(Duration.ofMillis(ms));
    }
}

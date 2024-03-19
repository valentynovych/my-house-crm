package com.example.myhouse24user.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateConverter {

    private final static ZoneId ZONE_ID = ZoneId.systemDefault();
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static String instantToString(Instant date) {
        LocalDate localDate = LocalDate.ofInstant(date, ZONE_ID);
        return localDate.format(DATE_TIME_FORMATTER);
    }

    public static Instant stringToInstant(String date) {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.parse(date, DATE_TIME_FORMATTER),
                LocalTime.now());
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZONE_ID);
        return zonedDateTime.toInstant();
    }
}

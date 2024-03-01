package com.example.myhouse24admin.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateConverter {
    public static String instantToString(Instant date){
        LocalDate localDate = LocalDate.ofInstant(date, ZoneId.systemDefault());
        return localDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
    public static Instant stringToInstant(String date){
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                LocalTime.now());
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return zonedDateTime.toInstant();
    }
}

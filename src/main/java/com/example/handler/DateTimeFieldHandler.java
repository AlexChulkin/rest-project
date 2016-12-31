package com.example.handler;

import org.exolab.castor.mapping.GeneralizedFieldHandler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by alexc_000 on 2016-12-29.
 */
public class DateTimeFieldHandler extends GeneralizedFieldHandler {
    public static final String dateFormatPattern = "yyyy-MM-dd hh:mm:ss";
    private static final Locale DEFAULT_LOCALE = Locale.getDefault();
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    private static final DateTimeFormatter dateTimeFormatter;

    static {
        dateTimeFormatter = DateTimeFormatter
                .ofPattern(dateFormatPattern)
                .withLocale(DEFAULT_LOCALE)
                .withZone(DEFAULT_ZONE_ID);
    }

    public static String format(final Instant dateTime) {
        String dateTimeString = "";

        if (Optional.ofNullable(dateTime).isPresent()) {
            dateTimeString = dateTimeFormatter.format(dateTime);
        }

        return dateTimeString;
    }

    public static Instant parse(final String dateTimeString) {
        Instant instant = Instant.MIN;

        if (Optional.ofNullable(dateTimeString).isPresent()) {
            LocalDateTime dt = LocalDateTime.parse(dateTimeString, dateTimeFormatter);
            ZonedDateTime zdt = dt.atZone(DEFAULT_ZONE_ID);
            instant = zdt.toInstant();
        }

        return instant;
    }

    @Override
    public Object convertUponGet(Object value) {
        Instant dateTime = (Instant) value;

        return format(dateTime);
    }

    @Override
    public Object convertUponSet(Object value) {
        String dateTimeString = (String) value;

        return parse(dateTimeString);
    }

    @Override
    public Class<Instant> getFieldType() {
        return Instant.class;
    }
}


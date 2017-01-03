package com.example.serialization.xml;

import org.exolab.castor.mapping.GeneralizedFieldHandler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

import static com.example.config.ConfigurationConstants.DATE_TIME_FORMATTER;
import static com.example.config.ConfigurationConstants.DEFAULT_ZONE_ID;

/**
 * Created by alexc_000 on 2016-12-29.
 */
public class DateTimeFieldHandler extends GeneralizedFieldHandler {


    public static String format(final Instant dateTime) {
        String dateTimeString = "";

        if (Optional.ofNullable(dateTime).isPresent()) {
            dateTimeString = DATE_TIME_FORMATTER.format(dateTime);
        }

        return dateTimeString;
    }

    public static Instant parse(final String dateTimeString) {
        Instant instant = Instant.MIN;

        if (Optional.ofNullable(dateTimeString).isPresent()) {
            LocalDateTime dt = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
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


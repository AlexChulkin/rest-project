package com.example.config;

import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Created by alexc_000 on 2017-01-03.
 */
@Component
public class ConfigurationConstants {
    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER;
    public static final Locale DEFAULT_LOCALE = Locale.UK;
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("UTC");
    public static final int MAX_NAME_LENGTH = 60;
    public static final String DEFAULT_PAGE_SIZE = "5";
    public static final String DEFAULT_PAGE_INDEX = "0";




    static {
        DATE_TIME_FORMATTER = DateTimeFormatter
                .ofPattern(DATE_TIME_FORMAT_PATTERN)
                .withLocale(DEFAULT_LOCALE)
                .withZone(DEFAULT_ZONE_ID);
    }
}

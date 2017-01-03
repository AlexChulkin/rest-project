package com.example.config;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Created by alexc_000 on 2017-01-03.
 */
public class ConfigurationConstants {
    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd hh:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER;
    public static final Locale DEFAULT_LOCALE = Locale.getDefault();
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
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

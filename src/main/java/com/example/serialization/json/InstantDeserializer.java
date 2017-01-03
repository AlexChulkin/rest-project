package com.example.serialization.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static com.example.config.ConfigurationConstants.DATE_TIME_FORMATTER;
import static com.example.config.ConfigurationConstants.DEFAULT_ZONE_ID;

/**
 * Created by alexc_000 on 2016-12-31.
 */

public class InstantDeserializer extends JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        LocalDateTime dt = LocalDateTime.parse(p.getValueAsString(), DATE_TIME_FORMATTER);
        ZonedDateTime zdt = dt.atZone(DEFAULT_ZONE_ID);
        return zdt.toInstant();
    }
}

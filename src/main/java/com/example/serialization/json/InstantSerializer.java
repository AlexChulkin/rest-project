package com.example.serialization.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Instant;

import static com.example.config.ConfigurationConstants.DATE_TIME_FORMATTER;


/**
 * Created by alexc_000 on 2016-12-31.
 */
public class InstantSerializer extends JsonSerializer<Instant> {
    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(DATE_TIME_FORMATTER.format(value));
    }
}

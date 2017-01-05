package com.example.components;

import com.example.serialization.json.BigDecimalDeserializer;
import com.example.serialization.json.BigDecimalSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexc_000 on 2017-01-04.
 */
@Component
public class CustomObjectMapper extends ObjectMapper {
    @Autowired
    BigDecimalDeserializer bigDecimalDeserializer;

    @Autowired
    BigDecimalSerializer bigDecimalSerializer;
}

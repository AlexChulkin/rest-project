package com.example;

import com.example.serialization.json.InstantDeserializer;
import com.example.serialization.json.InstantSerializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@SpringBootApplication
public class RestProjectApplication {
    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd hh:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER;
    public static final Locale DEFAULT_LOCALE = Locale.getDefault();
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    public static final List<MediaType> SUPPORTED_MEDIA_TYPES = Arrays.asList(MediaType.APPLICATION_XML);

    static {
        DATE_TIME_FORMATTER = DateTimeFormatter
                .ofPattern(DATE_TIME_FORMAT_PATTERN)
                .withLocale(DEFAULT_LOCALE)
                .withZone(DEFAULT_ZONE_ID);
    }

    public static void main(String[] args) {
        SpringApplication.run(RestProjectApplication.class, args);
    }


    /**
     * Enable @Valid validation exception validation for @PathVariable, @RequestParam and @RequestHeader.
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(serializingObjectMapper());
        return jsonConverter;
    }

    @Bean
    public ObjectMapper serializingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Instant.class, new InstantSerializer());
        javaTimeModule.addDeserializer(Instant.class, new InstantDeserializer());
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public MarshallingHttpMessageConverter marshallingHttpMessageConverter() {
        MarshallingHttpMessageConverter marshallingHttpMessageConverter = new MarshallingHttpMessageConverter();
        marshallingHttpMessageConverter.setMarshaller(castorMarshaller());
        marshallingHttpMessageConverter.setUnmarshaller(castorMarshaller());
        marshallingHttpMessageConverter.setSupportedMediaTypes(SUPPORTED_MEDIA_TYPES);
        return marshallingHttpMessageConverter;
    }

    @Bean
    public CastorMarshaller castorMarshaller() {
        CastorMarshaller castorMarshaller = new CastorMarshaller();
        castorMarshaller.setMappingLocation(new ClassPathResource("static/oxm-mapping.xml"));
        return castorMarshaller;
    }
}

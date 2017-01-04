package com.example.endpoint;

import com.example.serialization.json.BigDecimalDeserializer;
import com.example.serialization.json.BigDecimalSerializer;
import com.example.serialization.json.InstantDeserializer;
import com.example.serialization.json.InstantSerializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.Instant;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by alexc_000 on 2017-01-01.
 */
public class AbstractEndpointTest {
    protected static final MediaType JSON_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("UTF-8"));
    protected static final MediaType XML_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_XML.getType(), MediaType.APPLICATION_XML.getSubtype(), Charset.forName("UTF-8"));

    @Autowired
    protected WebApplicationContext webApplicationContext;
    protected MockMvc mockMvc;
    //    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    CastorMarshaller castorMarshaller;

    protected void setup() throws Exception {

        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Instant.class, new InstantSerializer());
        javaTimeModule.addDeserializer(Instant.class, new InstantDeserializer());
        objectMapper.registerModule(javaTimeModule);
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        module.addDeserializer(BigDecimal.class, new BigDecimalDeserializer());
        objectMapper.registerModule(module);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Returns json representation of the object.
     *
     * @param o instance
     * @return json
     * @throws IOException
     */
    protected String json(Object o) throws IOException {

        return objectMapper.writeValueAsString(o);
    }

    protected String xml(Object o) throws IOException {
        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        castorMarshaller.marshal(o, sr);
        return sw.toString();
    }
}

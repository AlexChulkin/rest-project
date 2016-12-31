package com.example.domain;


import com.example.handler.DateTimeFieldHandler;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Created by alexc_000 on 2016-12-29.
 */
@NamedQueries({
        @NamedQuery(name = "Product.findProductsByName",
                query = "SELECT NEW com.example.domain.Product(p, 'WOUT_NAME') " +
                        "FROM Product p WHERE p.name = :name"),
        @NamedQuery(name = "Product.findProductsByTimestamp",
                query = "SELECT NEW com.example.domain.Product(p, 'WOUT_TIMESTAMP') " +
                        "FROM Product p WHERE p.timestamp = :timestamp"),
})
@Entity
@Table(name = "product", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "timestamp"}))
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    transient EntityCompleteness entityCompleteness = EntityCompleteness.COMPLETE;

    private String name;
    @JsonSerialize(using = OmitTimeStampSerializer.class, as = Instant.class)
    private Instant timestamp;
    @JsonSerialize(using = OmitNameSerializer.class, as = BigDecimal.class)
    private BigDecimal price;
    private int version;
    private Long id;

    public Product() {
    }

    public Product(Product p, String entityCompleteness) {
        setPrice(p.getPrice());
        setTimestamp(p.getTimestamp());
        setName(p.getName());
        setEntityCompleteness(EntityCompleteness.valueOf(entityCompleteness));
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 11)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull(message = "error.name.notNull")
    @Size(min = 1, max = 60, message = "error.name.size")
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull(message = "error.timestamp.notNull")
    @DateTimeFormat(pattern = DateTimeFieldHandler.dateFormatPattern)
    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @NotNull(message = "error.price.notNull")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Version
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public EntityCompleteness getEntityCompleteness() {
        return entityCompleteness;
    }

    public void setEntityCompleteness(EntityCompleteness entityCompleteness) {
        this.entityCompleteness = entityCompleteness;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

    public class OmitNameSerializer extends JsonSerializer<String> {
        public OmitNameSerializer() {
        }

        @Override
        public void serialize(String name,
                              JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider)
                throws IOException {
            if (getEntityCompleteness() != EntityCompleteness.WOUT_NAME) {
                jsonGenerator.writeObject(name);
            }
        }
    }

    public class OmitTimeStampSerializer extends JsonSerializer<Instant> {

        public OmitTimeStampSerializer() {
        }

        @Override
        public void serialize(Instant timestamp,
                              JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider)
                throws IOException {
            if (getEntityCompleteness() != EntityCompleteness.WOUT_TIMESTAMP) {
                jsonGenerator.writeObject(timestamp);
            }
        }
    }
}


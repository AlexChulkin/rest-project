package com.example.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import static com.example.config.ConfigurationConstants.DATE_TIME_FORMAT_PATTERN;
import static com.example.config.ConfigurationConstants.MAX_NAME_LENGTH;

/**
 * Created by alexc_000 on 2016-12-29.
 */
@NamedQueries({
        @NamedQuery(name = "Product.findProductsByName",
                query = "SELECT NEW com.example.domain.TimestampAndPrice(p.timestamp, p.price) " +
                        "FROM Product p WHERE p.name = :name"),
        @NamedQuery(name = "Product.findProductsByTimestamp",
                query = "SELECT NEW com.example.domain.NameAndPrice(p.name, p.price) " +
                        "FROM Product p WHERE year(p.timestamp) = year(:timestamp) AND " +
                        "month(p.timestamp) = month(:timestamp) AND " +
                        "day(p.timestamp) = day(:timestamp) AND day(p.timestamp) = day(:timestamp) AND " +
                        "minute(p.timestamp) = minute(:timestamp) AND second(p.timestamp) = second(:timestamp)"),
})
@Entity
@Table(name = "product", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "timestamp"}))
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;

    @JsonFormat(pattern = DATE_TIME_FORMAT_PATTERN)
    private Instant timestamp;
    private BigDecimal price;
    private int version;
    private Long id;

    public Product(String name, Instant timestamp, BigDecimal price) {
        this.name = name;
        this.timestamp = timestamp;
        this.price = price;
    }

    public Product() {
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

    @NotBlank(message = "{error.product.name.blank}")
    @Length(max = MAX_NAME_LENGTH, message = "{error.product.name.length}")
    @Column
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull(message = "{error.product.timestamp.null}")
    @DateTimeFormat(pattern = DATE_TIME_FORMAT_PATTERN)
    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @NotNull(message = "{error.product.price.null}")
    @PriceValid
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}


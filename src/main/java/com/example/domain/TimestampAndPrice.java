package com.example.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Created by alexc_000 on 2016-12-30.
 */
public class TimestampAndPrice {
    private Instant timestamp;
    private BigDecimal price;

    public TimestampAndPrice() {
    }

    public TimestampAndPrice(Instant timestamp, BigDecimal price) {
        this.timestamp = timestamp;
        this.price = price;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean equalsToProduct(Product product) {
        return this.timestamp.equals(product.getTimestamp())
                && (this.price.compareTo(product.getPrice()) == 0);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}

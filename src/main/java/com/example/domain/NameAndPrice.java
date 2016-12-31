package com.example.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * Created by alexc_000 on 2016-12-30.
 */
public final class NameAndPrice {
    private String name;
    private BigDecimal price;

    public NameAndPrice() {
    }

    public NameAndPrice(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean equalsToProduct(Product product) {
        return StringUtils.equalsIgnoreCase(this.getName(), product.getProductName())
                && (this.price.compareTo(product.getPrice()) == 0);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}

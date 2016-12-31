package com.example.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by alexc_000 on 2016-12-29.
 */
public class Products implements Serializable {
    private List<NameAndPrice> nameAndPriceList = Collections.emptyList();
    private List<TimestampAndPrice> timestampAndPriceList = Collections.emptyList();

    public Products() {
    }

    public List<NameAndPrice> getNameAndPriceList() {
        return nameAndPriceList;
    }

    public Products setNameAndPriceList(List<NameAndPrice> nameAndPriceList) {
        this.nameAndPriceList = nameAndPriceList;
        return this;
    }

    public List<TimestampAndPrice> getTimestampAndPriceList() {
        return timestampAndPriceList;
    }

    public Products setTimestampAndPriceList(List<TimestampAndPrice> timestampAndPriceList) {
        this.timestampAndPriceList = timestampAndPriceList;
        return this;
    }
}

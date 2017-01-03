package com.example.serialization.xml;

import org.exolab.castor.mapping.GeneralizedFieldHandler;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by alexc_000 on 2017-01-03.
 */
public class BigDecimalFieldHandler extends GeneralizedFieldHandler {

    public static String format(final BigDecimal bigDecimal) {
        String bigDecimalString = "";

        if (Optional.ofNullable(bigDecimal).isPresent()) {
            bigDecimalString = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        }

        return bigDecimalString;
    }

    public static BigDecimal parse(final String bigDecimalString) {
        BigDecimal bigDecimal = BigDecimal.ZERO;

        if (Optional.ofNullable(bigDecimalString).isPresent()) {
            bigDecimal = new BigDecimal(bigDecimalString).setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        return bigDecimal;
    }

    @Override
    public Object convertUponGet(Object value) {
        BigDecimal bigDecimal = (BigDecimal) value;

        return format(bigDecimal);
    }

    @Override
    public Object convertUponSet(Object value) {
        String bigDecimalString = (String) value;

        return parse(bigDecimalString);
    }

    @Override
    public Class<BigDecimal> getFieldType() {
        return BigDecimal.class;
    }
}

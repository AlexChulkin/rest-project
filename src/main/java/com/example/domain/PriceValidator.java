package com.example.domain;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * Created by alexc_000 on 2017-01-02.
 */
public class PriceValidator implements ConstraintValidator<PriceValid, BigDecimal> {
    private static final BigDecimal MINIMAL_VALUE = BigDecimal.valueOf(0.01);
    private static final BigDecimal MAXIMAL_VALUE = BigDecimal.valueOf(9_999_999_999.99);
    private static final int SCALE = 2;

    @Override
    public void initialize(PriceValid paramA) {
    }

    @Override
    public boolean isValid(BigDecimal price, ConstraintValidatorContext ctx) {
        return !((price.compareTo(MINIMAL_VALUE) < 0) || (price.compareTo(MAXIMAL_VALUE) > 0)) && price.scale() == SCALE;

    }
}

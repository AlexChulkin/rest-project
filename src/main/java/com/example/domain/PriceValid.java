package com.example.domain;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static com.example.config.ConfigurationConstants.ERROR_PRODUCT_PRICE_INVALID;

/**
 * Created by alexc_000 on 2017-01-02.
 */
@Documented
@Constraint(validatedBy = PriceValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PriceValid {

    String message() default ERROR_PRODUCT_PRICE_INVALID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

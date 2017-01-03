package com.example.domain;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by alexc_000 on 2017-01-02.
 */
@Documented
@Constraint(validatedBy = PriceValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PriceValid {

    String message() default "{error.product.validation.price}";
//    String message() default "Price must be not less than 0.01 and not bigger than 9 999 999 999.99. Scale must be exactly 2.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

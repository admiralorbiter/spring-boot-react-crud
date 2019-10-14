package com.dorukdestan.interview.eestienergia.datevalidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BeforeDateValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Before {
    String message() default "Date should be before now";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
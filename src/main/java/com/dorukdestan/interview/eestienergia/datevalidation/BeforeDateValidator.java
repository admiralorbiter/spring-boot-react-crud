package com.dorukdestan.interview.eestienergia.datevalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BeforeDateValidator implements ConstraintValidator<Before, LocalDate> {

	@Override
	public void initialize(Before constraintAnnotation) {

	}

	@Override
	public boolean isValid(LocalDate value, ConstraintValidatorContext context) {

		if (value == null) {
			return true; // Being not-null should be checked explicitly by @NotNull validation
		}

		return value.isBefore(LocalDate.now());
	}
}
package com.omarahmed42.user.validation.validator;

import java.time.LocalDate;
import java.time.Period;

import com.omarahmed42.user.validation.annotation.ValidAdult;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AdultValidator implements ConstraintValidator<ValidAdult, LocalDate> {
    @Override
    public void initialize(ValidAdult constraintAnnotation) {
        // Do nothing because we don't need to initialize anything
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value != null && value.isBefore(LocalDate.now().minus(Period.ofYears(18)));
    }

}

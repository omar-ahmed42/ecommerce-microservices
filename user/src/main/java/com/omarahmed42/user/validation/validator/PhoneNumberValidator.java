package com.omarahmed42.user.validation.validator;

import com.omarahmed42.user.validation.annotation.ValidPhoneNumber;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        // Do nothing because we don't need to initialize anything
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches("\\d+") && (value.length() > 8) && (value.length() < 12);
    }
}

package com.omarahmed42.user.validation.validator;

import com.omarahmed42.user.validation.annotation.ValidName;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<ValidName, String> {

    private String fieldName;

    @Override
    public void initialize(ValidName constraintAnnotation) {
        this.fieldName = constraintAnnotation.fieldName();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(fieldName + " must not be empty")
                    .addConstraintViolation();
            return false;
        } else if (!value.matches("^[A-Za-z ]{2,50}$")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    fieldName + " must contain only letters and spaces, and be between 2 and 50 characters long")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}

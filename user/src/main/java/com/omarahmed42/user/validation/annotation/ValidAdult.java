package com.omarahmed42.user.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.omarahmed42.user.validation.validator.AdultValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AdultValidator.class)
public @interface ValidAdult {
    String message() default "Must be 18 or older";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


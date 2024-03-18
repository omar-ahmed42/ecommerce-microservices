package com.omarahmed42.user.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.omarahmed42.user.validation.validator.NameValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NameValidator.class)
public @interface ValidName {
    String message() default "Invalid name";
    String fieldName() default "Name";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


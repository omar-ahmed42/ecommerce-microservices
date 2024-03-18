package com.omarahmed42.user.validation.validator;

import java.util.Arrays;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.RuleResult;

import com.omarahmed42.user.validation.annotation.ValidPassword;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private org.passay.PasswordValidator validator;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        validator = new org.passay.PasswordValidator(Arrays.asList(
                new LengthRule(8, 256),
                new CharacterRule(EnglishCharacterData.UpperCase, 1), // At least 1 upper case letter
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1)));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password cannot be null").addConstraintViolation();
            return false;
        }

        RuleResult result = validator.validate(new PasswordData(value));
        if (result.isValid())
            return true;

        /*
         * disables the default constraint violation that would otherwise show the
         * generic message defined in the annotation
         */
        context.disableDefaultConstraintViolation();

        /*
         * builds a new constraint violation with a template that joins all the messages
         * returned by the validator for each failed rule using a comma as a separator
         */
        context.buildConstraintViolationWithTemplate(
                String.join(", ", validator.getMessages(result)))
                .addConstraintViolation();
        return false;
    }
}
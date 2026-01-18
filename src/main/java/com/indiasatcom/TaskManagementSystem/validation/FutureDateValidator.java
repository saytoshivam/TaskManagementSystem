package com.indiasatcom.TaskManagementSystem.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/**
 * Validator for FutureDate annotation.
 * Ensures the date is valid and in the future.
 */
public class FutureDateValidator implements ConstraintValidator<FutureDate, LocalDate> {

    @Override
    public void initialize(FutureDate constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null validation
        }
        // Check if date is in the future (after today)
        return value.isAfter(LocalDate.now());
    }
}


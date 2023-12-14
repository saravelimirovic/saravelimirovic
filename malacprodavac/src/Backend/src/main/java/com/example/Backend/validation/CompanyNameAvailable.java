package com.example.Backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {CompanyNameAvailableValidator.class})
public @interface CompanyNameAvailable {
    String message() default "name: Company with entered name already exists.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

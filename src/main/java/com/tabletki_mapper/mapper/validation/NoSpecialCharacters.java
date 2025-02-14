package com.tabletki_mapper.mapper.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NoSpecialCharactersValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoSpecialCharacters {
    String message() default "Invalid characters in input";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

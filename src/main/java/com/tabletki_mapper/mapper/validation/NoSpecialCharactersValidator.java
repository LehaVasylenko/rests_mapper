package com.tabletki_mapper.mapper.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoSpecialCharactersValidator implements ConstraintValidator<NoSpecialCharacters, String> {

    @Override
    public void initialize(NoSpecialCharacters constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String regex = "^[a-zA-Z0-9\\p{L}-]*$";
        String disallowedChars = "ыЫъЪьЬіІїЇ";

        for (char c : value.toCharArray()) {
            if (disallowedChars.indexOf(c) >= 0) {
                return false;
            }
        }
        return value.matches(regex);
    }
}


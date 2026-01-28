package com.lingulu.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Class<?> clazz = value.getClass();
            Field firstField = clazz.getDeclaredField(firstFieldName);
            Field secondField = clazz.getDeclaredField(secondFieldName);

            firstField.setAccessible(true);
            secondField.setAccessible(true);

            Object firstVal = firstField.get(value);
            Object secondVal = secondField.get(value);

            if (firstVal == null || secondVal == null) return true;

            boolean matches = firstVal.equals(secondVal);
            if (!matches) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode(secondFieldName)
                        .addConstraintViolation();
            }

            return matches;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}


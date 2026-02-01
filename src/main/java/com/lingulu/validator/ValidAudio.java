package com.lingulu.validator;

import java.lang.annotation.Retention;
import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AudioFileValidator.class)
public @interface ValidAudio {

    String message() default "Invalid audio file";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

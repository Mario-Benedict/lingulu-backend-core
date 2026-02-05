package com.lingulu.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AvatarFileValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAvatar {

    String message() default "Invalid avatar file";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


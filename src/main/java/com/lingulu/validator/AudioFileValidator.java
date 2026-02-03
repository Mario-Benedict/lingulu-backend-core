package com.lingulu.validator;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AudioFileValidator implements ConstraintValidator<ValidAudio, MultipartFile> {

    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

    @Override
    public void initialize(ValidAudio constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null &&
               (contentType.startsWith("audio/")
                || contentType.equals("application/octet-stream"));
    }
}


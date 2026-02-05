package com.lingulu.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class AvatarFileValidator implements ConstraintValidator<ValidAvatar, MultipartFile> {

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "webp");

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2 MB

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if (file == null || file.isEmpty()) {
            return true; // @NotNull yang handle
        }

        context.disableDefaultConstraintViolation();

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            context.buildConstraintViolationWithTemplate(
                    "Invalid avatar file extension"
            ).addConstraintViolation();
            return false;
        }

        String extension = originalFilename
                .substring(originalFilename.lastIndexOf('.') + 1)
                .toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            context.buildConstraintViolationWithTemplate(
                    "Avatar file must be jpeg, jpg, png, or webp"
            ).addConstraintViolation();
            return false;
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            context.buildConstraintViolationWithTemplate(
                    "Avatar file size must not exceed 2 MB"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
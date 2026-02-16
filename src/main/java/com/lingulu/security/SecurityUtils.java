package com.lingulu.security;

import com.lingulu.entity.account.User;
import com.lingulu.exception.account.EmailNotVerifiedException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtils {

    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        // Handle User object principal (set by JwtAuthenticationFilter)
        if (principal instanceof User user) {
            // Double-check email verification (should already be checked in filter)
            if (!user.isEmailVerified()) {
                throw new EmailNotVerifiedException(
                    "Email not verified. Please verify your email to access this resource.",
                    HttpStatus.FORBIDDEN
                );
            }
            return user.getUserId();
        }

        // Handle String userId principal (legacy/fallback)
        if (principal instanceof String userId) {
            return UUID.fromString(userId);
        }

        throw new RuntimeException("Invalid authentication principal");
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            // Verify email
            if (!user.isEmailVerified()) {
                throw new EmailNotVerifiedException(
                    "Email not verified. Please verify your email to access this resource.",
                    HttpStatus.FORBIDDEN
                );
            }
            return user;
        }

        throw new RuntimeException("User object not found in authentication principal");
    }

    public static boolean isEmailVerified() {
        try {
            User user = getCurrentUser();
            return user.isEmailVerified();
        } catch (Exception e) {
            return false;
        }
    }
}

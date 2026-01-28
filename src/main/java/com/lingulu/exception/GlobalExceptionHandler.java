package com.lingulu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.lingulu.dto.ApiResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppExceptions(AppException ex) {
        if (ex.getErrors() != null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), ex.getErrors()));
        }

        return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmptyBody(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "Request body cannot be empty", null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();

            // buat list baru kalau belum ada
            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
        });

        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "Validation Error", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllExceptions(Exception ex) {
        ex.printStackTrace(); // log error
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ApiResponse<>(false, "Internal Server Error", null));
    }
}

package com.lingulu.exception;

import org.springframework.http.HttpStatusCode;

import java.util.List;
import java.util.Map;

public class RegisterException extends AppException {
    public RegisterException(String message, HttpStatusCode status) {
        super(message, status);
    }

    public RegisterException(String message, Map<String, List<String>> errors) {
        super(message, errors);
    }
}

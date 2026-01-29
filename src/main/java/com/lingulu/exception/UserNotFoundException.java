package com.lingulu.exception;

import org.springframework.http.HttpStatusCode;

public class UserNotFoundException extends AppException {
    public UserNotFoundException(String message, HttpStatusCode status) {
        super(message, status);
    }
}

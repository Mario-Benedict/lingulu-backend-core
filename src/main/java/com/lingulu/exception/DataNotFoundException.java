package com.lingulu.exception;

import org.springframework.http.HttpStatusCode;

public class DataNotFoundException extends AppException {
    public DataNotFoundException(String message, HttpStatusCode status) {
        super(message, status);
    }
}

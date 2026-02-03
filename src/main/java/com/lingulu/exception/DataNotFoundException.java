package com.lingulu.exception;

import org.springframework.http.HttpStatusCode;

import java.util.List;
import java.util.Map;

public class DataNotFoundException extends AppException {
    public DataNotFoundException(String message, HttpStatusCode status) {
        super(message, status);
    }
}

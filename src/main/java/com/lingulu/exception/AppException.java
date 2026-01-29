package com.lingulu.exception;

import org.springframework.http.HttpStatusCode;

import java.util.List;
import java.util.Map;

public abstract class AppException extends RuntimeException {

    private Map<String, List<String>> errors;
    protected HttpStatusCode status;

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Map<String, List<String>> errors) {
        super(message);
        this.errors = errors;
    }

    public AppException(String message, HttpStatusCode status) {
        super(message);
        this.status = status;
    }

    public AppException(String message, Map<String, List<String>> errors, HttpStatusCode status) {
        super(message);
        this.errors = errors;
        this.status = status;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public HttpStatusCode getStatus() {
        return status;
    }
}
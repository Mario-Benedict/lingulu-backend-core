package com.lingulu.exception;

import java.util.List;
import java.util.Map;

public abstract class AppException extends RuntimeException {

    private Map<String, List<String>> errors;

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Map<String, List<String>> errors) {
        super(message);
        this.errors = errors;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}
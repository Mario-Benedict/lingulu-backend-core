package com.lingulu.exception;

import java.util.List;
import java.util.Map;

public class RegisterException extends AppException {
    public RegisterException(String message) {
        super(message);
    }

    // message + errors per field
    public RegisterException(String message, Map<String, List<String>> errors) {
        super(message, errors);
    }
}

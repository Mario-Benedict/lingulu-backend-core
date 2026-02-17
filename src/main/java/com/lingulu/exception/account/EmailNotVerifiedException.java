package com.lingulu.exception.account;

import org.springframework.http.HttpStatus;

public class EmailNotVerifiedException extends RuntimeException {
    private final HttpStatus httpStatus;

    public EmailNotVerifiedException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}


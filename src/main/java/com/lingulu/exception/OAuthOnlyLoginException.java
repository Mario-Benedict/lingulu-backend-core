package com.lingulu.exception;

import org.springframework.http.HttpStatusCode;

public class OAuthOnlyLoginException extends AppException {
    public OAuthOnlyLoginException(String message, HttpStatusCode status) {
        super(message, status);
    }
}

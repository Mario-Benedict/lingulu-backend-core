package com.lingulu.exception;

import org.springframework.http.HttpStatusCode;

public class OtpServiceException extends AppException {
    public OtpServiceException(String message) {
        super(message);
    }
}

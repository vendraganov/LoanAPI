package com.example.loan_api.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class IdempotentKeyExistException extends RuntimeException {

    private final HttpStatus status;
    private String methodName;

    public IdempotentKeyExistException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public IdempotentKeyExistException(String message, String methodName, HttpStatus status) {
        super(message);
        this.methodName = methodName;
        this.status = status;
    }
}

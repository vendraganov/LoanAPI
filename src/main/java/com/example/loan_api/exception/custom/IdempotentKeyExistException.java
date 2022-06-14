package com.example.loan_api.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class IdempotentKeyExistException extends RuntimeException {

    private final HttpStatus status;
    private String returnType;

    public IdempotentKeyExistException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public IdempotentKeyExistException(String message, String returnType, HttpStatus status) {
        super(message);
        this.returnType = returnType;
        this.status = status;
    }
}

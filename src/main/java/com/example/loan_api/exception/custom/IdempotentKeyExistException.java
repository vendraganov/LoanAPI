package com.example.loan_api.exception.custom;

public class IdempotentKeyExistException extends RuntimeException {
    public IdempotentKeyExistException(String message) {
        super(message);
    }
}

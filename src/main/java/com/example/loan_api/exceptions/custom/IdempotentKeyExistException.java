package com.example.loan_api.exceptions.custom;

public class IdempotentKeyExistException extends RuntimeException {
    public IdempotentKeyExistException(String message) {
        super(message);
    }
}

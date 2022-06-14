package com.example.loan_api.exception.custom;

public class LoanPaymentException extends RuntimeException {
    public LoanPaymentException(String message) {
        super(message);
    }
}

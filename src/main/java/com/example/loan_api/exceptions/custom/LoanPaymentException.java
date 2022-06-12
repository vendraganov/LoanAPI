package com.example.loan_api.exceptions.custom;

public class LoanPaymentException extends RuntimeException {
    public LoanPaymentException(String message) {
        super(message);
    }
}

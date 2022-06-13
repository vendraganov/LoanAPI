package com.example.loan_api.model.dto;

import com.example.loan_api.model.loan.LoanType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class LoanDTO {

    private UUID userId;
    private String name;
    private UUID loanId;
    private String paymentStatus;
    private LoanType loanType;
}

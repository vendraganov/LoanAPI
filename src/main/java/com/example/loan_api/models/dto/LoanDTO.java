package com.example.loan_api.models.dto;

import com.example.loan_api.models.loan.LoanType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class LoanDTO {

    private UUID userId;
    private String name;
    private UUID loanId;
    private LoanType loanType;
}

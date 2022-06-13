package com.example.loan_api.models.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ScheduleDTO {
    private String paymentDate;
    private String monthlyPayment;
    private String principal;
    private String interest;
    private String totalInterestPaid;
    private String remainingBalance;
    private String paymentStatus;
}

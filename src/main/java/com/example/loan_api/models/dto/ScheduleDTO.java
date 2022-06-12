package com.example.loan_api.models.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ScheduleDTO {
    private String paymentDate;
    private Double monthlyPayment;
    private Double principal;
    private Double interest;
    private Double totalInterestPaid;
    private Double remainingBalance;
    private String paymentStatus;
}

package com.example.loan_api.models.dto;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public abstract class AbstractPaymentDTO {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID loanId;
}

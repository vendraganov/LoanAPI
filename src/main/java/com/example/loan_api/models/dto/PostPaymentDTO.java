package com.example.loan_api.models.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostPaymentDTO {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID loanId;
}

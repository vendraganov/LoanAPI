package com.example.loan_api.model.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLoanDTO {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID loanTypeId;
}

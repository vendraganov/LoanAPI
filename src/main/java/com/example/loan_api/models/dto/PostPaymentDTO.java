package com.example.loan_api.models.dto;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostPaymentDTO extends AbstractPaymentDTO {

    @NotNull
    private Double amount;

    @Nullable
    private Double fine;
}

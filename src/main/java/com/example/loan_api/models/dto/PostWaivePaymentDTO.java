package com.example.loan_api.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostWaivePaymentDTO extends AbstractPaymentDTO {

    @NotNull
    private Boolean waivePayment;
}

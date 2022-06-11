package com.example.loan_api.models.responses;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Error {

    private String domain;
    private String reason;
    private String message;
}

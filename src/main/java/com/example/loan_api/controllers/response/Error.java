package com.example.loan_api.controllers.response;

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

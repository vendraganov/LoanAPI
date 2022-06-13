package com.example.loan_api.model.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Data
public class UserDTO {

    private UUID id;
    private String name;
    private String token;
}

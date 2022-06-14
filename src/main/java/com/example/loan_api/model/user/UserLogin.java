package com.example.loan_api.model.user;


import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLogin {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;
}

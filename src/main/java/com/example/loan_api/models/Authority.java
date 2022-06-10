package com.example.loan_api.models;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static com.example.loan_api.helpers.Constants.AUTHORITY_ID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authorities")
public class Authority implements GrantedAuthority {

    private static final String ROLE = "role";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = AUTHORITY_ID)
    private Long authorityId;

    @NotNull
    @Column(name = ROLE)
    private String role;

    @Override
    public String getAuthority() {
        return role;
    }
}

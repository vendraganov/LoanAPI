package com.example.loan_api.models.auth;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.UUID;

import static com.example.loan_api.helpers.Constants.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authorities")
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID)
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = ROLE)
    private Role role;

    @Override
    public String getAuthority() {
        return role.name();
    }
}

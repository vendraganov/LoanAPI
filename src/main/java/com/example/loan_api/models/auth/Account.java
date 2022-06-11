package com.example.loan_api.models.auth;

import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.example.loan_api.helpers.Constants.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="accounts")
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID)
    private UUID id;

    @NotNull
    @Email
    @Column(name = EMAIL, unique = true)
    private String email;

    @NotNull
    @Column(name = PASSWORD)
    private String password;

    @NotNull
    @OneToOne
    @JoinColumn(name = FK_AUTHORITY)
    private Authority authority;

    @Override
    public  List<Authority> getAuthorities() {
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

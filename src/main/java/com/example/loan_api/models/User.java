package com.example.loan_api.models;

import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

import static com.example.loan_api.helpers.Constants.AUTHORITY_ID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User implements UserDetails {

    private static final String USER_ID = "user_id";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String ENABLED = "enabled";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = USER_ID)
    private Long userId;

    @NotNull
    @Email
    @Column(name = EMAIL, unique = true)
    private String email;

    @NotNull
    @Column(name = PASSWORD)
    private String password;

    @NotNull
    @OneToOne
    @JoinColumn(name = AUTHORITY_ID)
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

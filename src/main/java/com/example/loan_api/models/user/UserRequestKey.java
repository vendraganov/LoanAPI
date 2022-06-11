package com.example.loan_api.models.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.loan_api.helpers.Constants.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="idempotent_keys")
public class UserRequestKey {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID)
    private UUID id;

    @NotNull
    @Column(name = REQUEST_KEY)
    private UUID requestKey;

    @NotNull
    @Column(name = DOMAIN)
    private UUID domain;

    @NotNull
    @Column(name = USED_ON)
    private LocalDateTime usedOn;
}
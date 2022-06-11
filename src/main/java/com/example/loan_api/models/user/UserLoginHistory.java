package com.example.loan_api.models.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.loan_api.helpers.Constants.FK_USER;
import static com.example.loan_api.helpers.Constants.ID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="user_login_history")
public class UserLoginHistory {

    private static final String LOGIN_ON = "login_on";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID)
    private UUID id;

    @NotNull
    @Column(name = LOGIN_ON)
    private LocalDateTime loginOn;

    @NotNull
    @OneToOne
    @JoinColumn(name = FK_USER)
    private User user;
}

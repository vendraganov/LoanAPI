package com.example.loan_api.models.loan;


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
@Table(name = "loan_fines")
public class LoanFine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID)
    private UUID id;

    @NotNull
    @Column(name = AMOUNT)
    private Double amount;

    @NotNull
    @Column(name = FINED_ON)
    private LocalDateTime finedOn;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = FK_LOAN)
    private Loan loan;
}

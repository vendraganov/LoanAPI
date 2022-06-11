package com.example.loan_api.models.loan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static com.example.loan_api.helpers.Constants.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="loan_types")
public class LoanType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID)
    private UUID id;

    @NotNull
    @Column(name = TYPE)
    private String type;

    @NotNull
    @Column(name = MONTHS)
    private Integer months;

    @NotNull
    @Column(name = AMOUNT)
    private Double amount;

    @NotNull
    @Column(name = INTEREST)
    private Double interest;
}

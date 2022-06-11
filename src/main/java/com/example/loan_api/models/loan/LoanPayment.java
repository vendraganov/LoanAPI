package com.example.loan_api.models.loan;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

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
@Table(name="loan_payments")
public class LoanPayment {

    @Id
    @GeneratedValue(generator = UUID)
    @GenericGenerator(name = UUID, strategy = UUID_GENERATOR)
    @Type(type = UUID_TYPE)
    @Column(name = ID)
    private UUID id;

    @NotNull
    @Column(name = AMOUNT)
    private Double amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = TYPE)
    private LoanPaymentType type;

    @NotNull
    @Column(name = PAYED_ON)
    private LocalDateTime payedOn;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = FK_LOAN)
    private Loan loan;
}

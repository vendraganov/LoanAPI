package com.example.loan_api.models.loan;


import com.example.loan_api.helpers.Constants;
import com.example.loan_api.models.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.loan_api.helpers.Constants.*;

@Builder
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
    @Column(name = PRINCIPAL, scale = 2)
    private BigDecimal principal;

    @NotNull
    @Column(name = INTEREST, scale = 2)
    private BigDecimal interest;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = Constants.STATUS)
    private LoanPaymentStatus status;

    @NotNull
    @Column(name = PAID_ON)
    private LocalDateTime paidOn;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = FK_LOAN)
    private Loan loan;

    @JsonIgnore
    @NotNull
    @OneToOne
    @JoinColumn(name = FK_USER)
    private User user;
}

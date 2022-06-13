package com.example.loan_api.models.loan;

import com.example.loan_api.models.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.loan_api.helpers.Constants.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="loans")
public class Loan {

    @Id
    @GeneratedValue(generator = UUID)
    @GenericGenerator(name = UUID, strategy = UUID_GENERATOR)
    @Type(type = UUID_TYPE)
    @Column(name = ID)
    private UUID id;

    @NotNull
    @Column(name = MONTHLY_PAYMENT_AMOUNT, scale = 2)
    private BigDecimal monthlyPaymentAmount;

    @NotNull
    @Column(name = WAIVED_PAYMENT, columnDefinition = DEFAULT_FALSE)
    private Boolean waivedPayment;

    @NotNull
    @Column(name = APPLIED_ON)
    private LocalDateTime appliedOn;

    @NotNull
    @Column(name = APPROVED_ON)
    private LocalDateTime approvedOn;

    @Nullable
    @Column(name = PAID_OFF_ON)
    private LocalDateTime paidOffOn;

    @Nullable
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = LOAN)
    private List<LoanPayment> loanPayments;

    @NotNull
    @OneToOne
    @JoinColumn(name = FK_LOAN_TYPE)
    private LoanType loanType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = FK_USER)
    private User user;
}

package com.example.loan_api.models.loan;

import com.example.loan_api.models.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


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
    @Column(name = PAYMENT_WAIVED, columnDefinition = DEFAULT_FALSE)
    private Boolean paymentWaived;

    @NotNull
    @Column(name = APPLIED_ON)
    private LocalDateTime appliedOn;

    @Nullable
    @Column(name = PAYED_OFF_ON)
    private LocalDateTime payedOffOn;

    @Nullable
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = LOAN)
    private List<LoanPayment> loanPayments;

    @Nullable
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = LOAN)
    private List<LoanFine> loanFines;

    @NotNull
    @OneToOne
    @JoinColumn(name = FK_LOAN_TYPE)
    private LoanType loanType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = FK_USER)
    private User user;
}

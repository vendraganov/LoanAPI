package com.example.loan_api.model.loan;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

import static com.example.loan_api.helper.Constants.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="loan_types")
public class LoanType {

    @Id
    @GeneratedValue(generator = UUID)
    @GenericGenerator(name = UUID, strategy = UUID_GENERATOR)
    @Type(type = UUID_TYPE)
    @Column(name = ID)
    private UUID id;

    @NotNull
    @Column(name = TYPE)
    private String type;

    @NotNull
    @Column(name = MONTHS)
    private Integer months;

    @NotNull
    @Column(name = AMOUNT, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = INTEREST, scale = 2)
    private BigDecimal interest;
}

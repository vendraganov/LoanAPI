package com.example.loan_api.models.user;


import com.example.loan_api.models.auth.Account;
import com.example.loan_api.models.loan.Loan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

import static com.example.loan_api.helpers.Constants.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(generator = UUID)
    @GenericGenerator(name = UUID, strategy = UUID_GENERATOR)
    @Type(type = UUID_TYPE)
    @Column(name = ID)
    private UUID id;

    @Column(name = NAME)
    private String name;

    @NotNull
    @OneToOne
    @JoinColumn(name = FK_ACCOUNT)
    private Account account;

    @Nullable
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = USER)
    private List<Loan> loans;
}

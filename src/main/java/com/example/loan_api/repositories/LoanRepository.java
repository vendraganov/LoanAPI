package com.example.loan_api.repositories;

import com.example.loan_api.models.loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
}

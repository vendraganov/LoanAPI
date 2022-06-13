package com.example.loan_api.repository;

import com.example.loan_api.model.loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findAllByUserId(UUID userId);
    Optional<Loan> findByIdAndUserId(UUID loanId, UUID userId);
}

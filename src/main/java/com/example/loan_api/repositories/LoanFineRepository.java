package com.example.loan_api.repositories;

import com.example.loan_api.models.loan.LoanFine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanFineRepository extends JpaRepository<LoanFine, UUID> {
}

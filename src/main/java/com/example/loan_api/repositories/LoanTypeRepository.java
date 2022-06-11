package com.example.loan_api.repositories;

import com.example.loan_api.models.loan.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanTypeRepository extends JpaRepository<LoanType, UUID> {
}

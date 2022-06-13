package com.example.loan_api.repository;

import com.example.loan_api.model.loan.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoanTypeRepository extends JpaRepository<LoanType, UUID> {
}
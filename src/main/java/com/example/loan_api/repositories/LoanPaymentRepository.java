package com.example.loan_api.repositories;

import com.example.loan_api.models.loan.LoanPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoanPaymentRepository extends JpaRepository<LoanPayment, UUID> {
}

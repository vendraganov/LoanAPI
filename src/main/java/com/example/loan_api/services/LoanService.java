package com.example.loan_api.services;

import com.example.loan_api.models.loan.Loan;
import com.example.loan_api.models.loan.LoanType;
import com.example.loan_api.repositories.LoanFineRepository;
import com.example.loan_api.repositories.LoanPaymentRepository;
import com.example.loan_api.repositories.LoanRepository;
import com.example.loan_api.repositories.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final LoanFineRepository loanFineRepository;

    public List<LoanType> getTypes() {
        return this.loanTypeRepository.findAll();
    }

    //ToDO implement logic for getting the schedule
    public List<Loan> getSchedule() {
        return this.loanRepository.findAll();
    }
}

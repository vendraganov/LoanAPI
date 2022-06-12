package com.example.loan_api.services;

import com.example.loan_api.models.dto.LoanDTO;
import com.example.loan_api.models.dto.PostLoanDTO;
import com.example.loan_api.models.dto.PostPaymentDTO;
import com.example.loan_api.models.dto.PostWaivePaymentDTO;
import com.example.loan_api.models.loan.Loan;
import com.example.loan_api.models.loan.LoanType;
import com.example.loan_api.models.user.User;
import com.example.loan_api.repositories.LoanFineRepository;
import com.example.loan_api.repositories.LoanPaymentRepository;
import com.example.loan_api.repositories.LoanRepository;
import com.example.loan_api.repositories.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class LoanService {

    private static final String LOAN_NOT_FOUND = "Loan type not found with id: ";
    private static final String LOAN_EXIST = "Already applied for this loan!";

    private final LoanRepository loanRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final LoanFineRepository loanFineRepository;
    private final UserService userService;

    public List<LoanDTO> getLoans(UUID userId) {
        User user = this.userService.findById(userId);
        return this.loanRepository.findAllByUserId(userId)
                .stream()
                .map(loan -> LoanDTO.builder()
                        .userId(user.getId())
                        .name(user.getName())
                        .loanId(loan.getId())
                        .loanType(loan.getLoanType())
                        .build()).collect(Collectors.toList());
    }

    public List<LoanType> getTypes() {
        return this.loanTypeRepository.findAll();
    }

    public List<Loan> getSchedule(UUID loanId, UUID userId) {
        return this.loanRepository.findAll();
    }

    public void apply(PostLoanDTO postLoanDTO) {
        User user = this.userService.findById(postLoanDTO.getUserId());
        LoanType loanType = this.getLoanType(postLoanDTO.getLoanTypeId());
        if (this.hasUserAppliedForThisLoanType(user, loanType)) {
            throw new IllegalArgumentException(LOAN_EXIST);
        }
        Loan loan = Loan.builder()
                .paymentWaived(false)
                .appliedOn(LocalDateTime.now())
                .loanType(loanType)
                .user(user)
                .build();
        this.loanRepository.save(loan);
    }

    public void payment(PostPaymentDTO postPaymentDTO) {

    }

    public void waivePayment(PostWaivePaymentDTO postWaivePaymentDTO) {

    }

    private LoanType getLoanType(UUID loanTypeId) {
        return this.loanTypeRepository.findById(loanTypeId)
                .orElseThrow(() -> new IllegalArgumentException(LOAN_NOT_FOUND));
    }

    private boolean hasUserAppliedForThisLoanType(User user, LoanType loanType) {
        return user.getLoans() != null && user.getLoans()
                .stream()
                .flatMap(loan -> Stream.of(loan.getLoanType()))
                .anyMatch(loanType1 -> loanType1.getId().equals(loanType.getId()));
    }
}

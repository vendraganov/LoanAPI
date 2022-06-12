package com.example.loan_api.services;

import com.example.loan_api.exceptions.custom.LoanPaymentException;
import com.example.loan_api.models.dto.*;
import com.example.loan_api.models.loan.Loan;
import com.example.loan_api.models.loan.LoanPayment;
import com.example.loan_api.models.loan.LoanPaymentStatus;
import com.example.loan_api.models.loan.LoanType;
import com.example.loan_api.models.user.User;
import com.example.loan_api.repositories.LoanPaymentRepository;
import com.example.loan_api.repositories.LoanRepository;
import com.example.loan_api.repositories.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Service
public class LoanService {

    private static final String LOAN_NOT_FOUND = "Loan not found with id: ";
    private static final String LOAN_TYPE_NOT_FOUND = "Loan type not found with id: ";
    private static final String LOAN_EXIST = "Already applied for this loan!";
    private static final String LOAN_IS_PAID_OFF = "Loan is paid off!";
    private static final String WAIVE_PAYMENT_APPLIED = "Waived Payment applied to this loan!";
    private static final String DATE_FORMATTER = " dd-MMM-yyyy";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    private final LoanRepository loanRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanPaymentRepository loanPaymentRepository;
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

    public List<ScheduleDTO> getSchedule(UUID loanId) {
        Loan loan = this.getLoan(loanId);
        List<LoanPayment> loanPayments = loan.getLoanPayments();
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        double principalAmountPaid = 0;
        double totalInterestPaid = 0;
        int payments = loanPayments == null ? 0 : loanPayments.size();

        for (int i = 0; i < loan.getLoanType().getMonths(); i++) {
            String paymentStatus = i < payments ? loanPayments.get(i).getStatus().name() : LoanPaymentStatus.UNPAID.name();
            double interest =  i < payments ? this.calculateMonthlyInterest(loan) : this.calculateMonthlyInterest(loan.getLoanType().getAmount(), principalAmountPaid, loan.getLoanType().getInterest());
            double principal = round(loan.getMonthlyPaymentAmount() - interest);
            principalAmountPaid += principal;
            totalInterestPaid += interest;
            ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                    .paymentDate(loan.getApprovedOn().plusMonths(i+1).format(formatter))
                    .monthlyPayment(round(loan.getMonthlyPaymentAmount()))
                    .principal(principal)
                    .interest(interest)
                    .totalInterestPaid(round(totalInterestPaid))
                    .remainingBalance(round(loan.getLoanType().getAmount()-principalAmountPaid))
                    .paymentStatus(paymentStatus)
                    .build();
            scheduleDTOS.add(scheduleDTO);
        }
        return scheduleDTOS;
    }

    public void apply(PostLoanDTO postLoanDTO) {
        User user = this.userService.findById(postLoanDTO.getUserId());
        LoanType loanType = this.getLoanType(postLoanDTO.getLoanTypeId());
        if (this.hasUserAppliedForThisLoanType(user, loanType)) {
            throw new IllegalArgumentException(LOAN_EXIST);
        }
        Double monthlyPaymentAmount = this.calculateMonthlyPaymentAmount(loanType.getAmount(), loanType.getMonths(), loanType.getInterest());
        Loan loan = Loan.builder()
                .monthlyPaymentAmount(monthlyPaymentAmount)
                .waivedPayment(false)
                .appliedOn(LocalDateTime.now())
                .approvedOn(LocalDateTime.now().plusDays(3))
                .loanType(loanType)
                .user(user)
                .build();
        this.loanRepository.save(loan);
    }

    @Transactional
    public void payment(PostPaymentDTO postPaymentDTO, boolean waivedPayment) {
        User user = this.userService.findById(postPaymentDTO.getUserId());
        Loan loan = this.getLoan(postPaymentDTO.getLoanId());
        LoanPaymentStatus loanPaymentStatus = waivedPayment ? LoanPaymentStatus.WAIVED :  LoanPaymentStatus.PAID;
        this.checkIsLoanPaidOff(loan);
        if (waivedPayment) {
            this.checkIsWaivePaymentApplied(loan);
            loan.setWaivedPayment(true);
        }
        LoanPayment loanPayment = this.savePayment(user, loan, loanPaymentStatus);
        this.addPaymentToLoan(loan, loanPayment);
        this.setLoanPaidOffIfPaymentIsLast(loan);
        this.loanRepository.save(loan);
    }

    private LoanPayment savePayment(User user, Loan loan, LoanPaymentStatus loanPaymentStatus) {
        double interest = this.calculateMonthlyInterest(loan);
        double principal = round(loan.getMonthlyPaymentAmount() - interest);
        LoanPayment loanPayment = LoanPayment.builder()
                .principal(principal)
                .interest(interest)
                .status(loanPaymentStatus)
                .paidOn(LocalDateTime.now())
                .loan(loan)
                .user(user)
                .build();
       return this.loanPaymentRepository.save(loanPayment);
    }

    private void addPaymentToLoan(Loan loan, LoanPayment loanPayment) {
        if (loan.getLoanPayments() == null) {
            loan.setLoanPayments(Collections.singletonList(loanPayment));
        }
        loan.getLoanPayments().add(loanPayment);
    }

    private void setLoanPaidOffIfPaymentIsLast(Loan loan) {
        if (loan.getLoanPayments() != null && loan.getLoanType().getMonths() == loan.getLoanPayments().size()) {
            loan.setPaidOffOn(LocalDateTime.now());
        }
    }

    private LoanType getLoanType(UUID loanTypeId) {
        return this.loanTypeRepository.findById(loanTypeId)
                .orElseThrow(() -> new IllegalArgumentException(LOAN_TYPE_NOT_FOUND));
    }

    private Loan getLoan(UUID loanId) {
        return this.loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException(LOAN_NOT_FOUND));
    }

    private boolean hasUserAppliedForThisLoanType(User user, LoanType loanType) {
        return user.getLoans() != null && user.getLoans()
                .stream()
                .flatMap(loan -> Stream.of(loan.getLoanType()))
                .anyMatch(loanType1 -> loanType1.getId().equals(loanType.getId()));
    }

    private void checkIsLoanPaidOff(Loan loan) {
        if (loan.getPaidOffOn() != null) {
            throw new LoanPaymentException(LOAN_IS_PAID_OFF);
        }
    }

    private void checkIsWaivePaymentApplied(Loan loan) {
        if (loan.getWaivedPayment()) {
            throw new LoanPaymentException(WAIVE_PAYMENT_APPLIED);
        }
    }

    private double calculateMonthlyPaymentAmount(double amount, int months, double interest) {
        double i = ((interest/100)/12);
        return amount*(Math.pow((1+i), months)*i)/((Math.pow((1+i), months))-1);
    }

    private double calculateMonthlyInterest(Loan loan) {
        double totalAmount = loan.getLoanType().getAmount();
        double principalAmountPaid =  loan.getLoanPayments() == null || loan.getLoanPayments().isEmpty() ? 0 :
                loan.getLoanPayments().stream().mapToDouble(LoanPayment::getPrincipal).sum();
        return round(this.calculateMonthlyInterest(totalAmount, principalAmountPaid, loan.getLoanType().getInterest()));
    }

    private double calculateMonthlyInterest(double totalAmount, double principalAmountPaid, double interest) {
        return round((totalAmount-principalAmountPaid)*(interest/100/12));
    }

    private Double round(Double value) {
        return new BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

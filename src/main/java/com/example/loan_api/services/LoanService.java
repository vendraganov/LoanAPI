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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
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

    public List<LoanDTO> getAllByUserId(UUID userId) {
        User user = this.userService.findById(userId);
        return this.loanRepository.findAllByUserId(userId)
                .stream()
                .map(loan -> LoanDTO.builder()
                        .userId(user.getId())
                        .name(user.getName())
                        .loanId(loan.getId())
                        .loanType(loan.getLoanType())
                        .paymentStatus(loan.getPaidOffOn() == null ? LoanPaymentStatus.UNPAID.name() : LoanPaymentStatus.PAID.name())
                        .build()).collect(Collectors.toList());
    }

    public List<LoanType> getTypes() {
        return this.loanTypeRepository.findAll();
    }

    public List<ScheduleDTO> getSchedule(UUID loanId) {
        Loan loan = this.getLoan(loanId);
        List<LoanPayment> loanPayments = loan.getLoanPayments();
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();

        BigDecimal principalAmountPaid = BigDecimal.ZERO;
        BigDecimal totalInterestPaid = BigDecimal.ZERO;
        int payments = loanPayments == null ? 0 : loanPayments.size();

        for (int i = 0; i < loan.getLoanType().getMonths(); i++) {
            String paymentStatus = i < payments ? loanPayments.get(i).getStatus().name() : LoanPaymentStatus.UNPAID.name();
            BigDecimal interest =  i < payments ? this.calculateMonthlyInterest(loan) :
                    this.calculateMonthlyInterest(loan.getLoanType().getAmount(), principalAmountPaid, loan.getLoanType().getInterest());
            BigDecimal principal = loan.getMonthlyPaymentAmount().subtract(interest);
            principalAmountPaid = principalAmountPaid.add(principal);
            totalInterestPaid = totalInterestPaid.add(interest);
            ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                    .paymentDate(loan.getApprovedOn().plusMonths(i+1).format(formatter))
                    .monthlyPayment(round(loan.getMonthlyPaymentAmount()).toString())
                    .principal(round(principal).toString())
                    .interest(round(interest).toString())
                    .totalInterestPaid(round(totalInterestPaid).toString())
                    .remainingBalance(round(loan.getLoanType().getAmount().subtract(principalAmountPaid)).toString())
                    .paymentStatus(paymentStatus)
                    .build();
            scheduleDTOS.add(scheduleDTO);
        }
        return scheduleDTOS;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void apply(PostLoanDTO postLoanDTO) {
        User user = this.userService.findById(postLoanDTO.getUserId());
        LoanType loanType = this.getLoanType(postLoanDTO.getLoanTypeId());
        if (this.isUserAppliedForThisLoanType(user, loanType)) {
            throw new IllegalArgumentException(LOAN_EXIST);
        }
        BigDecimal monthlyPaymentAmount = this.calculateMonthlyPaymentAmount(loanType.getAmount(), loanType.getMonths(), loanType.getInterest());
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

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
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
        BigDecimal interest = this.calculateMonthlyInterest(loan);
        BigDecimal principal = loan.getMonthlyPaymentAmount().subtract(interest);
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

    private boolean isUserAppliedForThisLoanType(User user, LoanType loanType) {
        return user.getLoans() != null && user.getLoans()
                .stream()
                .filter(loan -> loan.getPaidOffOn() == null)
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

    private BigDecimal calculateMonthlyPaymentAmount(BigDecimal amount, Integer months, BigDecimal interestIn) {
        BigDecimal interest = this.getBigDecimalInterest(interestIn);
        BigDecimal pow = interest.add(BigDecimal.ONE).pow(months);
        return amount.multiply(interest.multiply(pow))
                .divide(pow.subtract(BigDecimal.ONE), 12, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMonthlyInterest(Loan loan) {
        BigDecimal totalAmount = loan.getLoanType().getAmount();
        BigDecimal principalAmountPaid =  loan.getLoanPayments() == null || loan.getLoanPayments().isEmpty() ? BigDecimal.ZERO :
                loan.getLoanPayments().stream().map(LoanPayment::getPrincipal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return this.calculateMonthlyInterest(totalAmount, principalAmountPaid, loan.getLoanType().getInterest());
    }

    private BigDecimal calculateMonthlyInterest(BigDecimal totalAmount, BigDecimal principalAmountPaid, BigDecimal interest) {
        return (totalAmount.subtract(principalAmountPaid).multiply(this.getBigDecimalInterest(interest)));
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getBigDecimalInterest(BigDecimal interestIn) {
        return interestIn.divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(12), 12, RoundingMode.HALF_UP);
    }
}
